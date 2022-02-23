package net.blancworks.figura.serving.dealers.backend.requests;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

public class EntityAvatarRequest extends DealerRequest {
    public final AvatarHolder holder;
    public final UUID id;
    public final FiguraBackendDealer.FiguraWebSocketClient client;

    private int currentAvatarIndex = 0;
    private int avatarCount = 0;

    public EntityAvatarRequest(AvatarHolder holder, UUID id, FiguraBackendDealer.FiguraWebSocketClient client) {
        this.holder = holder;
        this.id = id;
        this.client = client;
    }

    @Override
    protected void onSubmit() {
        currentAvatarIndex = 0; //Reset this in case of re-submissions.
        avatarCount = -1;
        client.avatarServer.requestAvatarList(this);
    }

    public void onAvatarObtained(UUID id, byte[] c) {
        if(avatarCount == -1){
            FiguraMod.LOGGER.error("Target avatar count is invalid! Did we get an avatar before we got a response, somehow?");
            isFinished = true; //Tag as finished so things don't clutter up.
            return;
        }

        int nextIndex = currentAvatarIndex++;

        //Only read avatars WITH data.
        if (c.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(c);
            DataInputStream dis = new DataInputStream(bis);

            try {
                NbtCompound avatarTag = NbtIo.readCompressed(dis);
                FiguraAvatar avatar = FiguraAvatar.getAvatar();

                FiguraAvatarNbtConverter.readNBT(avatar, avatarTag);

                holder.avatars[nextIndex] = avatar;
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

        //When all avatars are obtained, the task is complete.
        if (currentAvatarIndex == avatarCount)
            isFinished = true;
    }

    public void setAvatarCount(int count) {
        avatarCount = count;

        //If there are no avatars, this task is complete immediately.
        if(avatarCount == 0)
            isFinished = true;
    }
}
