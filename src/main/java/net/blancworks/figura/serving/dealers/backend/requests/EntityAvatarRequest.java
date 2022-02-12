package net.blancworks.figura.serving.dealers.backend.requests;

import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.entity.AvatarGroup;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class EntityAvatarRequest extends DealerRequest {
    private final AvatarGroup group;
    private final Entity targetEntity;
    private final FiguraBackendDealer.FiguraWebSocketClient client;

    public EntityAvatarRequest(AvatarGroup group, Entity entity, FiguraBackendDealer.FiguraWebSocketClient client) {
        this.group = group;
        this.targetEntity = entity;
        this.client = client;
    }

    @Override
    protected void onSubmit() {
        client.avatarServer.requestAvatars(this);
    }
}
