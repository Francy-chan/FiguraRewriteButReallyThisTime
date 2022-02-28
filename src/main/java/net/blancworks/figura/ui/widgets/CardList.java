package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.io.AvatarFileSet;
import net.blancworks.figura.avatar.io.ImporterManager;
import net.blancworks.figura.avatar.io.nbt.deserializers.FiguraAvatarDeserializer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.ui.cards.AvatarCardElement;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.Panel;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
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

    // Slider control
    private final ScrollBarWidget slider;

    // -- Constructors -- //

    public CardList(int x, int y, int width, int height) {
        super(x, y, width, height, LiteralText.EMPTY);

        slider = new ScrollBarWidget(x + width - 14, y + 4, 10, height - 8);
        addDrawableChild(slider);
    }

    // -- Functions -- //

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        loadAvatars();

        UIHelper.fillRound(matrices, x, y, width, height, 0x60000000);
        //UIHelper.renderBackgroundTexture(x, y, width, height, OPTIONS_BACKGROUND_TEXTURE);
        UIHelper.setupScissor(x, y, width, height);

        // Render each avatar tracker //

        //get list dimensions
        int cardWidth = 0;
        int cardHeight = 0;

        for (int i = 0, j = 72; i < avatarList.size(); i++, j += 72) {
            cardWidth = Math.max(cardWidth, j);

            //row check
            if (j + 72 > width - 12 - slider.getWidth()) {
                //reset j
                j = 0;

                //add height
                cardHeight += 104;
            }
        }

        //render cards

        int xOffset = (width - cardWidth + 8) / 2;
        int cardX = 0;
        int cardY;
        int id = 1;

        if (cardHeight + 104 < height) {
            cardY = 8;
        } else {
             cardY = (int) -(MathHelper.lerp(slider.getScrollProgress(), -8, cardHeight - (height - 104)));
        }

        for (AvatarTracker tracker : avatarList) {
            //stencil ID
            tracker.card.stencil.stencilLayerID = id++;
            if (id >= 256) id = 1;

            //draw card
            tracker.x = x + cardX + xOffset;
            tracker.y = y + cardY;

            if (tracker.y + tracker.getHeight() > this.y)
                tracker.render(matrices, mouseX, mouseY, delta);

            //update pos
            cardX += 72;
            if (cardX + 72 > width - 12 - slider.getWidth()) {
                cardX = 0;
                cardY += 104;

                if (cardY > this.x + this.height)
                    break;
            }
        }

        //set slider x
        slider.x = x + width - slider.getWidth() - 4;

        //reset scissor
        RenderSystem.disableScissor();

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void loadAvatars() {
        // Load avatars //
        HashMap<Path, AvatarFileSet> foundAvatars = ImporterManager.foundAvatars;

        //Reset missing paths
        missingPaths.clear();
        missingPaths.addAll(avatars.keySet());

        //Foreach avatar, if it's new, add it to load queue.
        for (Map.Entry<Path, AvatarFileSet> entry : foundAvatars.entrySet()) {
            missingPaths.remove(entry.getKey());
            avatars.computeIfAbsent(entry.getKey(), (p) -> {
                AvatarTracker a = new AvatarTracker(p, entry.getValue(), this);

                avatarList.add(a);
                addSelectableChild(a);

                return a;
            });
        }

        //Remove missing avatars
        for (Path missingPath : missingPaths) {
            AvatarTracker obj = avatars.remove(missingPath);
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
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isMouseOver(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return this.slider.mouseReleased(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.slider.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }

    public void updateHeight(int y, int height) {
        //update pos
        this.y = y;
        this.slider.y = y + 4;

        //update height
        this.height = height;
        this.slider.setHeight(height - 8);
    }

    // -- Nested Types -- //

    private static class AvatarTracker extends ClickableWidget {
        private final Panel parent;

        private final Path path;
        private final AvatarFileSet set;
        private final AvatarCardElement card;

        private FiguraAvatar avatar;

        private float scale = 1f;
        public Vec2f rotationTarget = new Vec2f(0, 0);
        private Vec2f rotation = new Vec2f(0, 0);

        private float rotationMomentum = 0;

        public static final Vec3f DEFAULT_COLOR = new Vec3f(0.24f, 0.42f, 0.68f);

        private AvatarTracker(Path path, AvatarFileSet set, Panel parent) {
            super(0, 0, 64, 96, LiteralText.EMPTY);
            this.parent = parent;

            this.path = path;
            this.set = set;
            this.card = new AvatarCardElement(getColor(set.metadata.cardColor), 0);

            this.card.name = new LiteralText(set.metadata.avatarName);
            this.card.author = new LiteralText(set.metadata.creatorName);
        }

        public static Vec3f getColor(String colorName) {
            return switch (colorName.toLowerCase()) {
                case "ace" -> ColorUtils.ACE_BLUE;
                case "fran" -> ColorUtils.FRAN_PINK;
                case "nice" -> ColorUtils.NICE;
                default -> ColorUtils.hexStringToRGB(colorName, DEFAULT_COLOR);
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

            if (this.parent.isMouseOver(mouseX, mouseY) && isMouseOver(mouseX, mouseY)) {
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
            if (!this.isMouseOver(mouseX, mouseY) || !this.parent.isMouseOver(mouseX, mouseY) || Math.abs(rotationMomentum) > 10)
                return false;

            //Re-load and re-equip
            load();
            FiguraLocalDealer.localPlayerAvatarHolder.avatars[0] = avatar;
            lastFileSet = set;

            //Re-load avatar so that the reference isn't kept
            load();

            rotationMomentum = Math.random() > 0.5f ? 360 : -360;

            return true;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
        }
    }
}
