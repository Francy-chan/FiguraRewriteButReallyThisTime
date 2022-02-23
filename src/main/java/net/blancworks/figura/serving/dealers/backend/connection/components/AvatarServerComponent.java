package net.blancworks.figura.serving.dealers.backend.connection.components;


import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.messages.MessageNames;
import net.blancworks.figura.serving.dealers.backend.requests.EntityAvatarRequest;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.blancworks.figura.utils.ByteBufferExtensions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.MathHelper;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;

public class AvatarServerComponent extends ConnectionComponent {
    // -- Variables -- //

    // -- Constructors -- //

    public AvatarServerComponent(FiguraBackendDealer.FiguraWebSocketClient dealer) {
        super(dealer);

        registerReader(MessageNames.AVATAR_UPLOAD, this::onUploadResponse);
        registerReader(MessageNames.USER_AVATAR_LIST, this::onAvatarListReceived);
        registerReader(MessageNames.AVATAR_DOWNLOAD, this::onAvatarReceived);
        registerReader(MessageNames.USER_AVATAR_UPDATED, this::onAvatarReceived);
    }

    // -- Functions -- //

    @Override
    public void tick() {
        super.tick();

        tickListRequests();
        tickAvatarDownloads();
    }

    // -- Uploading -- //

    private final Queue<Consumer<String>> uploadResponseQueue = new LinkedList<>();

    /**
     * Uploads an avatar to the backend.
     */
    public void uploadAvatar(byte[] data, Consumer<String> uploadResponse) {
        try (var ctx = getContext(MessageNames.AVATAR_UPLOAD)) {
            ctx.writer.writeInt(data.length);
            ctx.writer.write(data);

            ByteBufferExtensions.writeString(ctx.writer, "test");
            ByteBufferExtensions.writeString(ctx.writer, "this is a description :D");

            uploadResponseQueue.add(uploadResponse);
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }
    }

    /**
     * Called when the backend replies with a response for an upload.
     */
    private void onUploadResponse(ByteBuffer bytes) {
        boolean didSucceed = ((int) bytes.get() + 128) != 0;
        String responseMessage = "success";
        if (!didSucceed) {
            responseMessage = ByteBufferExtensions.readString(bytes);
            FiguraMod.LOGGER.info("Backend responded to upload with " + responseMessage);
        } else {
            var msg = ByteBufferExtensions.readString(bytes);
            FiguraMod.LOGGER.info("Backend responded to upload with " + msg);
        }


        uploadResponseQueue.poll().accept(responseMessage);
    }

    // -- Avatar List requests -- //
    private final Queue<EntityAvatarRequest> avatarListRequestQueue = new LinkedList<>();
    private final Queue<EntityAvatarRequest> avatarListResponseQueue = new LinkedList<>();

    /**
     * Requests a list of avatars for a given entity.
     */
    public void requestAvatarList(EntityAvatarRequest entityAvatarRequest) {
        avatarListRequestQueue.add(entityAvatarRequest);
    }

    /**
     * Used to construct the request packet for the backend in a batch, rather than many individual packets.
     * Should be better for the backend efficiency.
     */
    private void tickListRequests() {
        List<UUID> ids = new ArrayList<>();

        //Clear out request queue (up to 128 values)
        for (int i = 0; i < 128 && avatarListRequestQueue.size() > 0; i++) {
            EntityAvatarRequest request = avatarListRequestQueue.poll();
            //Add the ID of this entity to the request list.
            ids.add(request.id);
            //Move to "waiting for response" queue.
            avatarListResponseQueue.add(request);
        }

        sendAvatarListsRequest(ids);
    }

    /**
     * Sends the backend a list of player UUIDs we want to get the avatar lists for
     */
    private void sendAvatarListsRequest(List<UUID> ids) {
        if (ids.size() == 0) return;

        try (var ctx = getContext(MessageNames.USER_AVATAR_LIST)) {
            ctx.writer.writeInt(ids.size());

            //Write all the UUIDs we wanna request in order
            for (UUID id : ids)
                ByteBufferExtensions.writeString(ctx.writer, id.toString());
        } catch (IOException e) {
            FiguraMod.LOGGER.error(e);
        }
    }

    /**
     * Called once per avatar list when the backend sends a list of avatar UUIDs.
     */
    public void onAvatarListReceived(ByteBuffer bytes) {
        int count = MathHelper.clamp(bytes.getInt(), 0, FiguraDealer.MAX_AVATARS);
        var entityRequest = avatarListResponseQueue.poll();

        //Just in case.
        if (entityRequest == null) return;

        //Let the entity request know how many avatars we're requesting so it knows when to complete
        entityRequest.setAvatarCount(count);

        //Request each avatar from backend
        for (int i = 0; i < count; i++) {
            String idString = ByteBufferExtensions.readString(bytes);
            UUID id = UUID.fromString(idString);

            requestAvatar(id, (c) -> {
                //TODO - Cache avatar!
                entityRequest.onAvatarObtained(id, c);
            });
        }
    }

    // -- Avatar Downloading -- //

    private final Queue<AvatarDownloadRequest> avatarDownloadRequestQueue = new LinkedList<>();
    private final Queue<AvatarDownloadRequest> avatarDownloadResponseQueue = new LinkedList<>();
    private static final byte[] emptyAvatarBytes = new byte[0];

    /**
     * Requests an avatar from the backend.
     */
    public void requestAvatar(UUID avatarID, Consumer<byte[]> onReceived) {
        //TODO - Read from cache! If cached, we can just return here immediately.
        avatarDownloadRequestQueue.add(new AvatarDownloadRequest(avatarID, onReceived));
    }

    // Submit requests for avatar downloads as a batch, again, for backend health.
    private void tickAvatarDownloads() {
        List<UUID> ids = new ArrayList<>();

        //Clear out request queue (up to 128 values)
        for (int i = 0; i < 128 && avatarDownloadRequestQueue.size() > 0; i++) {
            AvatarDownloadRequest request = avatarDownloadRequestQueue.poll();
            //Add the ID of this entity to the request list.
            ids.add(request.id);
            //Move to "waiting for response" queue.
            avatarDownloadResponseQueue.add(request);
        }

        sendAvatarsRequest(ids);
    }

    /**
     * Requests a list of avatars from the backend
     */
    private void sendAvatarsRequest(List<UUID> ids) {
        if (ids.size() == 0) return;

        try (var ctx = getContext(MessageNames.AVATAR_DOWNLOAD)) {
            ctx.writer.writeInt(ids.size());
            for (UUID id : ids)
                ByteBufferExtensions.writeString(ctx.writer, id.toString());
        } catch (Exception e) {
            // Ignored
        }
    }

    /**
     * Called when backend sends us an avatar.
     */
    private void onAvatarReceived(ByteBuffer bytes) {
        byte[] avatarBytes = emptyAvatarBytes;

        int bytesForAvatar = MathHelper.clamp(bytes.getInt(), 0, 1024 * 100);
        if (bytesForAvatar != 0) {
            avatarBytes = new byte[bytesForAvatar];
            bytes.get(avatarBytes);
        }

        FiguraMod.LOGGER.info("Got an avatar of " + avatarBytes.length + " bytes");

        var response = avatarDownloadResponseQueue.poll();

        if (response == null) {
            FiguraMod.LOGGER.error("No avatar request exists! wtf?");
            return;
        }
        response.onComplete.accept(avatarBytes);
    }


    // -- Avatar Updates -- //

    //Called when backend tells us a user we've subscribed to has changed their avatars.
    public void onAvatarsUpdated(UUID sourceID, List<UUID> avatarIDs) {
        AvatarHolder holder = socket.backend.getHolder(sourceID);
        Arrays.fill(holder.avatars, null); //Clear existing avatars

        for (int i = 0; i < avatarIDs.size(); i++) {
            int finalI = i;

            //For each ID, request an avatar.
            requestAvatar(avatarIDs.get(i), bytes -> {
                //Only read avatars WITH data.
                if (bytes.length > 0) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    DataInputStream dis = new DataInputStream(bis);

                    try {
                        NbtCompound avatarTag = NbtIo.readCompressed(dis);
                        FiguraAvatar avatar = new FiguraAvatar();

                        FiguraAvatarNbtConverter.readNBT(avatar, avatarTag);

                        holder.avatars[finalI] = avatar;
                    } catch (Exception e) {
                        FiguraMod.LOGGER.error(e);
                    } finally {
                        try {
                            dis.close();
                            bis.close();
                        } catch (Exception e) {
                            FiguraMod.LOGGER.error(e);
                        }
                    }
                }
            });
        }
    }

    private record AvatarDownloadRequest(UUID id, Consumer<byte[]> onComplete) {
    }

}
