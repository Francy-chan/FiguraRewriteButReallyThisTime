package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.importing.AvatarFileSet;
import net.blancworks.figura.avatar.importing.ImporterManager;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.ui.cards.AvatarCardElement;
import net.blancworks.figura.ui.panels.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
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
    private final HashMap<Path, AvatarTracker> avatars = new HashMap<Path, AvatarTracker>();
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
    private TexturedButtonWidget expandButton = new TexturedButtonWidget(0, 0, 20, 20, 0, 0, 20, new Identifier("figura", "textures/gui/extend_icon.png"), 20, 40, btn -> toggleExpand());
    private ScrollBarWidget slider;

    // -- Constructors -- //

    public CardList(int x, int y, int width, int height) {
        super(x, y, width, height, new LiteralText("CARD LIST"));
        initialY = y;
        initialHeight = height;
        preciseHeight = height;

        slider = new ScrollBarWidget(x + width - 10, 0, 10, height, 20);

        addDrawableChild(expandButton);
        addDrawableChild(slider);
    }

    // -- Functions -- //

    private void toggleExpand() {
        isExpanded = !isExpanded;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        //Calculate position and height
        y = (float) MathHelper.lerp(1 - Math.pow(0.6f, delta), y, isExpanded ? 60 : initialY - 4);
        preciseHeight = (float) MathHelper.lerp(1 - Math.pow(0.6f, delta), preciseHeight, isExpanded ? initialHeight : 60);

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

            if(totalCardWidth == 0)
                totalCardWidth = cx;
        }

        int windowHeight = MinecraftClient.getInstance().getWindow().getHeight();
        float scale = (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
        int sx = (int) (x * scale);
        int sy = (int) (windowHeight - ((y + height) * scale));
        int sw = (int) (width * scale);
        int sh = (int) (height * scale);

        RenderSystem.enableScissor(sx, sy, sw, sh);
        sy = (int) (y * scale); // Fix y coord for top-down calculations

        float bonusY = -MathHelper.lerp(slider.getScrollProgress(), 0, totalCardHeight + 44);
        float bonusX = (width - totalCardWidth) / 2.0f;

        //Current X and Y coordinates for the cards
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
            tracker.setScissor(sx / scale, sy / scale, sw / scale, sh / scale);

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
        //matrices.translate(x, (int)y, 100);

        RenderSystem.disableScissor();

        expandButton.setPos((int) (x + (width / 2.0f) - (expandButton.getWidth() / 2.0f)), (int) (y - expandButton.getHeight()));
        slider.x = (int) (x + width - 5);
        slider.y = (int) y + 2;
        slider.setHeight(height);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.pop();
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

        float sx = 0;
        float sy = 0;
        float sw = 0;
        float sh = 0;

        private AvatarTracker(Path path, AvatarFileSet set) {
            super(0, 0, 64, 96, new LiteralText(""));
            this.path = path;
            this.set = set;
            this.card = new AvatarCardElement(new Vec3f(1, 1, 1), 0);
        }

        public void load() {
            NbtCompound compound = new NbtCompound();
            set.writeAvatarNBT(compound);

            avatar = FiguraAvatar.getAvatar();
            FiguraAvatarNbtConverter.readNBT(avatar, compound);

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
            card.setRotation(rotation.x, -rotation.y);
            card.render(matrices, mouseX, mouseY, delta);

            matrices.pop();
        }

        public void animate(float deltaTime, int mouseX, int mouseY) {
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
            if (mouseX >= sx && mouseX <= sx + sw && mouseY >= sy && mouseY <= sy + sh) {
                return super.mouseClicked(mouseX, mouseY, button);
            }

            return false;
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
