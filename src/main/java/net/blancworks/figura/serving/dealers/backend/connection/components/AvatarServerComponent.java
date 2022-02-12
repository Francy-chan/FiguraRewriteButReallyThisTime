package net.blancworks.figura.serving.dealers.backend.connection.components;


import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.messages.MessageNames;
import net.blancworks.figura.utils.ByteBufferExtensions;
import net.minecraft.util.math.MathHelper;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

public class AvatarServerComponent extends ConnectionComponent {
    // -- Variables -- //

    // -- Constructors -- //

    public AvatarServerComponent(FiguraBackendDealer.FiguraWebSocketClient dealer) {
        super(dealer);

        registerReader(MessageNames.AVATAR_PROVIDE, this::onAvatarsRecieved);
        registerReader(MessageNames.AVATAR_UPLOAD_RESPONSE, this::onUploadResponse);
    }

    // -- Functions -- //

    /**
     * Requests a list of avatars from the backend
     */
    public void requestAvatars(List<UUID> ids) {
        try (var ctx = getContext(MessageNames.AVATAR_DOWNLOAD_REQUEST)) {
            int realCount = MathHelper.clamp(ids.size(), 0, 256);
            ctx.writer.writeInt(realCount);

            for (int i = 0; i < realCount; i++) {
                UUID id = ids.get(i);

                ctx.writer.writeLong(id.getMostSignificantBits());
                ctx.writer.writeLong(id.getLeastSignificantBits());
            }
        } catch (Exception e) {
            // Ignored
        }
    }

    /**
     * Called by the backend when we've gotten a list of avatars
     */
    private void onAvatarsRecieved(ByteBuffer bytes) {
        int returnedCount = MathHelper.clamp(bytes.getInt(), 0, 256);
        for (int i = 0; i < returnedCount; i++) {
            int bytesForAvatar = MathHelper.clamp(bytes.getInt(), 0, 1024 * 100);
            if (bytesForAvatar == 0) continue;

            byte[] avatarBytes = new byte[bytesForAvatar];
            bytes.get(avatarBytes);

            FiguraMod.LOGGER.info("Got an avatar of " + avatarBytes.length + " bytes");
        }
    }

    public void uploadAvatar(byte[] data) {
        try (var ctx = getContext(MessageNames.AVATAR_UPLOAD_REQUEST)) {
            ctx.writer.writeInt(data.length);
            ctx.writer.write(data);

            ByteBufferExtensions.writeString(ctx.writer, "test");
            ByteBufferExtensions.writeString(ctx.writer, "this is a description :D");
        } catch (Exception e) {
            // Ignored
        }
    }

    private void onUploadResponse(ByteBuffer bytes) {
        boolean didSucceed = bytes.get() != 0;
        if(!didSucceed) {
            String responseMessage = ByteBufferExtensions.readString(bytes);
            FiguraMod.LOGGER.info("Backend responded to upload with " + responseMessage);
        } else {
            FiguraMod.LOGGER.info("Avatar uploaded successfully!");
        }
    }
}
