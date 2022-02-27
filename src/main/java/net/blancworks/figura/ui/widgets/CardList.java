package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.io.AvatarFileSet;
import net.blancworks.figura.avatar.io.ImporterManager;
import net.blancworks.figura.avatar.io.nbt.deserializers.FiguraAvatarDeserializer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.ui.cards.AvatarCardElement;
import net.blancworks.figura.ui.panels.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

import java.nio.file.Path;
import java.util.*;

public class CardList extends Panel implements Element {
    // -- Variables -- //
    private final HashMap<Path, AvatarTracker> avatars = new HashMap<>();
    private final ArrayList<AvatarTracker> avatarList = new ArrayList<>();
    private final HashSet<Path> missingPaths = new HashSet<>();

    public static AvatarFileSet lastFileSet;

    // Loading
    private Date lastLoadTime = new Date();

    // Expanding
    private boolean isExpanded = false;
    private float preciseHeight;
    private final float initialY;
    private final float initialHeight;
    private final float expandedHeight;
    private final TexturedButton expandButton = new TexturedButton(0, 0, 20, 20, 0, 0, 20, new Identifier("figura", "textures/gui/extend_icon.png"), 40, 40, btn -> toggleExpand());
    private final ScrollBarWidget slider;

    // -- Constructors -- //

    public CardList(int x, int y, int width, int height, int expandedHeight) {
        super(x, y, width, height, new LiteralText("CARD LIST"));
        initialY = y;
        initialHeight = height;
        preciseHeight = height;
        this.expandedHeight = expandedHeight;

        slider = new ScrollBarWidget(0, 0, 10, height, 20);

        addDrawableChild(expandButton);
        addDrawableChild(slider);
    }

    // -- Functions -- //

    private void toggleExpand() {
        isExpanded = !isExpanded();
        expandButton.setUV(isExpanded() ? 20 : 0, 0);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        //Calculate position and height
        y = (float) MathHelper.lerp(1 - Math.pow(0.6f, delta), y, isExpanded() ? 60 : initialY - 4);
        preciseHeight = (float) MathHelper.lerp(1 - Math.pow(0.6f, delta), preciseHeight, isExpanded() ? expandedHeight : initialHeight);

        height = (int) preciseHeight;

        // Load avatars //
        var foundAvatars = ImporterManager.foundAvatars;

        //Reset missing paths
        missingPaths.clear();
        missingPaths.addAll(avatars.keySet());

        //Foreach avatar, if it's new, add it to load queue.
        for (Map.Entry<Path, AvatarFileSet> entry : foundAvatars.entrySet()) {
            missingPaths.remove(entry.getKey());
            avatars.computeIfAbsent(entry.getKey(), (p) -> {
                var a = new AvatarTracker(p, entry.getValue());

                avatarList.add(a);
                addSelectableChild(a);

                return a;
            });
        }

        //Remove missing avatars
        for (Path missingPath : missingPaths) {
            var obj = avatars.remove(missingPath);
            avatarList.remove(obj);
            remove(obj);
        }

        //Load new avatars
        Date currDate = new Date();
        if (currDate.getTime() - lastLoadTime.getTime() > 100) {
            lastLoadTime = currDate;

            //Load next avatar
            for (Map.Entry<Path, AvatarTracker> entry : avatars.entrySet()) {
                if (entry.getValue().avatar == null) {
                    entry.getValue().load();
                    break;
                }
            }
        }


        // Render each avatar tracker //

        //Compute width and height for horizontal centering and scrollbar, respectively
        int totalCardWidth = 0;
        int totalCardHeight = 0;
        int horizontalCardCount = 0;

        {
            int cx = 0;
            int cc = 0;

            for (AvatarTracker tracker : avatarList) {
                cc++;
                // Position //
                cx += 64 + 4;

                if (cx + 64 > width - 20) {
                    if (totalCardHeight == 0) {
                        horizontalCardCount = cc;
                        totalCardWidth = cx;
                    }

                    cx = 0;

                    totalCardHeight += 104;
                }
            }

            if (totalCardWidth == 0)
                totalCardWidth = cx;
        }

        float bonusY = -MathHelper.lerp(slider.getScrollProgress(), 0, totalCardHeight + 44);
        float bonusX = (width - totalCardWidth) / 2.0f;

        //Current X and Y coordinates for the cards
        int sliderX = 0;
        int cardX = 0;
        int cardY = 0;
        int id = 1;

        for (AvatarTracker tracker : avatarList) {
            // Card //

            //Stencil ID
            tracker.card.stencil.stencilLayerID = id++;
            if (id >= 256) id = 1;

            //Draw card
            tracker.x = (int) (x + bonusX) + cardX;
            tracker.y = (int) (y + bonusY) + cardY;
            //tracker.setScissor(sx / scale, sy / scale, sw / scale, sh / scale);

            tracker.render(matrices, mouseX, mouseY, delta);

            // Position //
            cardX += 64 + 4;

            if (cardX + 64 > width - 20) {
                cardX = 0;
                cardY += 104;

                if ((y + cardY + bonusY) > MinecraftClient.getInstance().getWindow().getScaledHeight())
                    break;
            }
        }


        matrices.push();
        matrices.translate(0, 0, 100);

        //RenderSystem.disableScissor();

        expandButton.setPos((int) (x + (width / 2.0f) - (expandButton.getWidth() / 2.0f)), (int) (y - expandButton.getHeight()));
        slider.x = (int) ((x + bonusX) + cardX);
        slider.y = (int) y + 2;
        slider.setHeight(height - 4);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.pop();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    // -- Nested Types -- //

    private static class AvatarTracker extends ClickableWidget {
        public final Path path;
        public final AvatarFileSet set;
        public final AvatarCardElement card;

        public FiguraAvatar avatar;

        public float scale = 1.0f;
        public Vec2f rotationTarget = new Vec2f(0, 0);
        public Vec2f rotation = new Vec2f(0, 0);
        private boolean isSelected = false;

        public float rotationMomentum = 0;

        float sx = 0;
        float sy = 0;
        float sw = 0;
        float sh = 0;

        private AvatarTracker(Path path, AvatarFileSet set) {
            super(0, 0, 64, 96, new LiteralText(""));
            this.path = path;
            this.set = set;
            this.card = new AvatarCardElement(getColor(set.metadata.cardBack), 0);

            this.card.name = new LiteralText(set.metadata.avatarName);
            this.card.author = new LiteralText(set.metadata.creatorName);
        }

        public Vec3f getColor(String colorName) {
            return switch (colorName) {
                case "red" -> new Vec3f(1, 0.2f, 0.2f);
                case "green" -> new Vec3f(0.2f, 1, 0.2f);
                case "blue" -> new Vec3f(0.2f, 0.2f, 1);
                case "ace" -> new Vec3f(175 / 255.0f, 242 / 255.0f, 1);
                case "pink" -> new Vec3f(1, 114 / 255.0f, 183 / 255.0f);
                default -> new Vec3f(1, 1, 1);
            };
        }

        public void load() {
            NbtCompound compound = set.getAvatarNbt();

            avatar = FiguraAvatarDeserializer.getInstance().deserialize(compound);

            card.avatar = avatar;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            //super.renderButton(matrices, mouseX, mouseY, delta);
            matrices.push();

            matrices.translate(x + 32, y + 48, 100);
            matrices.scale(scale, scale, scale);

            animate(delta, mouseX, mouseY);

            card.entity = MinecraftClient.getInstance().player;
            card.setRotation(rotation.x + rotationMomentum, -rotation.y);
            card.render(matrices, mouseX, mouseY, delta);

            matrices.pop();
        }

        public void animate(float deltaTime, int mouseX, int mouseY) {

            rotationMomentum = (float) MathHelper.lerp((1 - Math.pow(0.8, deltaTime)), rotationMomentum, 0);

            if (isMouseOver(mouseX, mouseY)) {
                rotationTarget = new Vec2f(
                        ((mouseX - (x + 32)) / 32.0f) * 30,
                        ((mouseY - (y + 48)) / 48.0f) * 30
                );

                scale = (float) MathHelper.lerp(1 - Math.pow(0.2, deltaTime), scale, 1.2f);
                rotation = new Vec2f(
                        (float) MathHelper.lerp(1 - Math.pow(0.3, deltaTime), rotation.x, rotationTarget.x),
                        (float) MathHelper.lerp(1 - Math.pow(0.3, deltaTime), rotation.y, rotationTarget.y)
                );
            } else {
                scale = (float) MathHelper.lerp(1 - Math.pow(0.3, deltaTime), scale, 1f);
                rotation = new Vec2f(
                        (float) MathHelper.lerp(1 - Math.pow(0.6, deltaTime), rotation.x, 0),
                        (float) MathHelper.lerp(1 - Math.pow(0.6, deltaTime), rotation.y, 0)
                );
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {

            if (Math.abs(rotationMomentum) > 10)
                return false;

            //if (mouseX >= sx && mouseX <= sx + sw && mouseY >= sy && mouseY <= sy + sh) {
                //return super.mouseClicked(mouseX, mouseY, button);
            //}

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            super.onClick(mouseX, mouseY);

            //Re-load and re-equip
            load();
            FiguraLocalDealer.localPlayerAvatarHolder.avatars[0] = avatar;
            lastFileSet = set;

            //Re-load avatar so that the reference isn't kept
            load();

            rotationMomentum = Math.random() > 0.5f ? 360 : -360;
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            super.mouseMoved(mouseX, mouseY);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        //Set scissor area to ensure element isn't selected outside valid area
        public void setScissor(float sx, float sy, float sw, float sh) {
            this.sx = sx;
            this.sy = sy;
            this.sw = sw;
            this.sh = sh;
        }
    }
}
