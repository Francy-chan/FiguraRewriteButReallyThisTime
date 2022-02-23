package net.blancworks.figura.ui.cards;

import net.blancworks.figura.avatar.newavatar.NewFiguraAvatar;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.blancworks.figura.utils.RenderingUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class AvatarCardElement extends EntityCardElement<PlayerEntity> {
    private static final FiguraEntityMetadata OVERRIDE_METADATA = new FiguraEntityMetadata();
    private static final AvatarHolder OVERRIDE_GROUP = new AvatarHolder(new NewFiguraAvatar[FiguraDealer.MAX_AVATARS]);
    static {
        OVERRIDE_METADATA.addGroup(new Identifier("figura", "override"), OVERRIDE_GROUP);
    }

    public NewFiguraAvatar avatar;

    public AvatarCardElement(Vec3f color, int stencilID) {

        super(color, stencilID);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        RenderingUtils.overrideMetadata = OVERRIDE_METADATA;
        OVERRIDE_GROUP.avatars[0] = avatar;

        super.render(matrixStack, mouseX, mouseY, delta);
    }
}
