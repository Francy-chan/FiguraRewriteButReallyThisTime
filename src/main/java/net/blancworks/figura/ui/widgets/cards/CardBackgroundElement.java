package net.blancworks.figura.ui.widgets.cards;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Class to control the card background effects **/
public class CardBackgroundElement extends DrawableHelper {

    record Layer(Identifier texture, List<Animation> animations) {}
    record Animation(AnimationType type, float animationSpeed) {}

    public static final Layer MISSING = new CardBackgroundElement.Layer(MissingSprite.getMissingSpriteId(), new ArrayList<>());

    private final BackgroundType type;
    private final CardElement card;
    private final List<Layer> layers = new ArrayList<>();

    private int animationTick = 0;

    public CardBackgroundElement(BackgroundType type, CardElement card) {
        this.type = type;
        this.card = card;
        layers.addAll(BackgroundType.BACKGROUND_DATA.get(type));
    }

    //"register"-like for background types
    public enum BackgroundType {
        DEFAULT,
        CHEESE,
        CLOUDS,
        COOKIE,
        RAINBOW,
        INSCRYPTION,
        SPACE,
        FADE;

        public static final HashMap<BackgroundType, List<Layer>> BACKGROUND_DATA = new HashMap<>();
    }

    //animations
    public enum AnimationType {
        PARALLAX,
        SCROLL_X,
        SCROLL_Y,
        RAINBOW,
        FADE,
        TINT
    }

    public void tick() {
        animationTick++;
    }

    public void render(MatrixStack matrixStack) {
        //prepare render
        matrixStack.push();
        matrixStack.translate(-48f, -32f, 0f);

        for (Layer layer : this.layers) {
            //prepare background
            RenderSystem.setShaderTexture(0, layer.texture());
            matrixStack.push();

            //animation uv offsets
            float u = 0f;
            float v = 0f;

            //colors
            float r = 1f;
            float g = 1f;
            float b = 1f;
            float alpha = 1f;

            for (Animation animation : layer.animations) {
                float animationSpeed = animation.animationSpeed;
                switch (animation.type) {
                    case PARALLAX -> {
                        //fake parallax effect - thx wolfy
                        float x = MathHelper.clamp(((-card.rot.x * animationSpeed) / 90) * 48, -48, 48);
                        float y = MathHelper.clamp(((card.rot.y * animationSpeed) / 90) * 32, -32, 32);
                        matrixStack.translate(x, y, 0);
                    }
                    case SCROLL_X -> u = animationTick * animationSpeed;
                    case SCROLL_Y -> v = animationTick * animationSpeed;
                    case RAINBOW -> {
                        Vec3f color = ColorUtils.hsvToRGB(new Vec3f(animationTick * animationSpeed % 256 / 255f, 1f, 1f));
                        r = color.getX();
                        g = color.getY();
                        b = color.getZ();
                    }
                    case FADE -> alpha = MathHelper.sin(animationTick * animationSpeed * 0.3f) * 0.5f + 0.5f;
                    case TINT -> {
                        Vec3f color = this.card.color;
                        r = color.getX();
                        g = color.getY();
                        b = color.getZ();
                        alpha = animationSpeed;
                    }
                }
            }

            //set color/alpha
            RenderSystem.setShaderColor(r, g, b, alpha);

            //drawTexture(matrices, x, y, x size, y size, u offset, v offset, u size, v size, texture width, texture height)
            drawTexture(matrixStack, 0, 0, 160, 160, u % 160, v % 160, 160, 160, 160, 160);

            //reset color/alpha
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

            matrixStack.pop();
        }

        matrixStack.pop();
    }

    public BackgroundType getType() {
        return type;
    }
}
