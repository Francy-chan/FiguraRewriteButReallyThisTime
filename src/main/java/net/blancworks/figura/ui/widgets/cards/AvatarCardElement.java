package net.blancworks.figura.ui.widgets.cards;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.utils.RenderingUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.HashMap;

public class AvatarCardElement extends EntityCardElement<PlayerEntity> {
    private static final FiguraMetadata OVERRIDE_METADATA;
    private static final AvatarHolder OVERRIDE_GROUP = new AvatarHolder(new FiguraAvatar[FiguraDealer.MAX_AVATARS]);
    static {
        var overrideMap = new HashMap<Identifier, AvatarHolder>();
        overrideMap.put(new Identifier("figura", "override"), OVERRIDE_GROUP);
        OVERRIDE_METADATA = new FiguraMetadata(overrideMap);
        OVERRIDE_METADATA.setTrustContainer(TrustManager.get(new Identifier("group","local")));
    }

    public FiguraAvatar avatar;

    public AvatarCardElement(Vec3f color, int stencilID, Text name, Text author) {
        super(color, stencilID, name, author, null);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        RenderingUtils.overrideMetadata = OVERRIDE_METADATA;
        OVERRIDE_GROUP.entries[0] = avatar;

        super.render(matrixStack, mouseX, mouseY, delta);
    }
}
