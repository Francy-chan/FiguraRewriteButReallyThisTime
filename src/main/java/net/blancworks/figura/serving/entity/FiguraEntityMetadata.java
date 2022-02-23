package net.blancworks.figura.serving.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class stores the Figura state for a related entity.
 */
public class FiguraEntityMetadata<T extends Entity> {
    // -- Variables -- //
    public T targetEntity;

    private final Map<Identifier, AvatarHolder> groupsByDealerID = new HashMap<>();
    private final ArrayList<AvatarHolder> groupList = new ArrayList<>();


    // -- Constructors -- //
    public FiguraEntityMetadata() {
    }

    public FiguraEntityMetadata(T targetEntity) {
        this.targetEntity = targetEntity;
    }

    // -- Functions -- //

    public void addGroup(Identifier id, AvatarHolder group) {
        groupsByDealerID.put(id, group);
        groupList.add(group);
    }

    public AvatarHolder getGroupByID(Identifier id) {
        return groupsByDealerID.get(id);
    }


    public void tick() {
        for (AvatarHolder avatarGroup : groupList)
            if (avatarGroup != null) avatarGroup.tick(targetEntity);
    }

    public void render(float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        for (AvatarHolder avatarGroup : groupList)
            if (avatarGroup != null) avatarGroup.render(targetEntity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

}
