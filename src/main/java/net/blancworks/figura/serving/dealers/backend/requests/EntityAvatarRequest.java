package net.blancworks.figura.serving.dealers.backend.requests;

import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.entity.AvatarGroup;

import java.util.UUID;

public class EntityAvatarRequest extends DealerRequest {
    public final AvatarGroup group;
    public final UUID id;
    public final FiguraBackendDealer.FiguraWebSocketClient client;

    public EntityAvatarRequest(AvatarGroup group, UUID id, FiguraBackendDealer.FiguraWebSocketClient client) {
        this.group = group;
        this.id = id;
        this.client = client;
    }

    @Override
    protected void onSubmit() {
        //client.avatarServer.sendAvatarsRequest(this);
    }
}
