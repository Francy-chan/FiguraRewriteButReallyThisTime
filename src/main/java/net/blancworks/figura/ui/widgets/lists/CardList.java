package net.blancworks.figura.ui.widgets.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.io.AvatarFileSet;
import net.blancworks.figura.avatar.io.ImporterManager;
import net.blancworks.figura.avatar.io.nbt.deserializers.FiguraAvatarDeserializer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.ContextMenu;
import net.blancworks.figura.ui.widgets.TextField;
import net.blancworks.figura.ui.widgets.cards.AvatarCardElement;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

import java.nio.file.Path;
import java.util.*;

public class CardList extends AbstractList {

    // -- Variables -- //

    private final HashMap<Path, AvatarTracker> avatars = new HashMap<>();
    private final HashSet<Path> missingPaths = new HashSet<>();

    private final ArrayList<AvatarTracker> avatarList = new ArrayList<>();

    public static AvatarFileSet lastFileSet;

    protected AvatarTracker selectedEntry;

    // Loading
    private Date lastLoadTime = new Date();

    // Search bar
    private final TextField textField;
    private boolean hasSearchBar = false;
    private String filter = "";

    // -- Constructors -- //

    public CardList(int x, int y, int width, int height) {
        super(x, y, width, height);

        textField = new TextField(x + 4, y + 4, width - 8, 22, new TranslatableText("figura.gui.search"), s -> filter = s);
        textField.setVisible(false);
        children.add(textField);
    }

    // -- Functions -- //
    @Override
    public void tick() {
        loadContents();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + scissorsX, y + scissorsY, width + scissorsWidth, height + scissorsHeight);

        // Render each avatar tracker //

        //get list dimensions
        int cardWidth = 0;
        int cardHeight = 0;

        for (int i = 0, j = 72; i < avatarList.size(); i++, j += 72) {
            cardWidth = Math.max(cardWidth, j);

            //row check
            if (j + 72 > width - 12 - scrollBar.getWidth()) {
                //reset j
                j = 0;

                //add height
                cardHeight += 104;
            }
        }

        //slider visibility
        scrollBar.visible = cardHeight + 104 > height;
        scrollBar.setScrollRatio(104, cardHeight + 104 - height);

        //render cards
        int xOffset = (width - cardWidth + 8) / 2;
        int cardX = 0;
        int cardY = scrollBar.visible ? (int) -(MathHelper.lerp(scrollBar.getScrollProgress(),  hasSearchBar ? -34 : -8, cardHeight - (height - 104))) : hasSearchBar ? 34 : 8;
        int id = 1;
        boolean hidden = false;

        for (AvatarTracker tracker : avatarList) {
            if (hidden) {
                tracker.visible = false;
                continue;
            }

            //stencil ID
            tracker.card.stencil.stencilLayerID = id++;
            if (id >= 256) id = 1;

            //draw card
            tracker.visible = true;
            tracker.x = x + cardX + xOffset;
            tracker.y = y + cardY;

            if (tracker.y + tracker.getHeight() > y + scissorsY)
                tracker.render(matrices, mouseX, mouseY, delta);

            //update pos
            cardX += 72;
            if (cardX + 72 > width - 12 - scrollBar.getWidth()) {
                cardX = 0;
                cardY += 104;

                if (cardY > height)
                    hidden = true;
            }
        }

        //reset scissor
        RenderSystem.disableScissor();

        //render children
        super.render(matrices, mouseX, mouseY, delta);

        //render overlays
        super.renderOverlays(matrices, mouseX, mouseY, delta);
    }

    private void loadContents() {
        // Load avatars //
        HashMap<Path, AvatarFileSet> foundAvatars = ImporterManager.foundAvatars;

        //Reset missing paths
        missingPaths.clear();
        missingPaths.addAll(avatars.keySet());

        //Foreach avatar, if it's new, add it to load queue.
        for (Map.Entry<Path, AvatarFileSet> entry : foundAvatars.entrySet()) {
            //filter
            if (!entry.getValue().metadata.avatarName.toLowerCase().contains(filter.toLowerCase()))
                continue;

            missingPaths.remove(entry.getKey());
            avatars.computeIfAbsent(entry.getKey(), (p) -> {
                AvatarTracker tracker = new AvatarTracker(p, entry.getValue(), this);

                avatarList.add(tracker);
                children.add(tracker);

                return tracker;
            });
        }

        //sort list
        avatarList.sort((avatar1, avatar2) -> avatar1.name.compareToIgnoreCase(avatar2.name));
        children.sort((children1, children2) -> {
            if (children1 instanceof AvatarTracker avatar1 && children2 instanceof AvatarTracker avatar2)
                return avatar1.name.compareToIgnoreCase(avatar2.name);
            return 0;
        });

        //Remove missing avatars
        for (Path missingPath : missingPaths) {
            AvatarTracker obj = avatars.remove(missingPath);
            avatarList.remove(obj);
            children.remove(obj);
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

    public AvatarTracker getSelectedEntry() {
        return selectedEntry;
    }

    public void updateHeight(int y, int height) {
        //update pos
        this.y = y;
        this.scrollBar.y = y + (hasSearchBar ? 30 : 4);

        //update height
        this.height = height;
        this.scrollBar.setHeight(height - (hasSearchBar ? 34 : 8));

        //search bar
        textField.setPos(this.x + 4, this.y + 4);

        //scissors
        this.updateScissors(1, hasSearchBar ? 26 : 1, -2, hasSearchBar ? -27 : -2);
    }

    public void toggleSearchBar(boolean bool) {
        this.hasSearchBar = bool;
        this.textField.setVisible(bool);
        this.scrollBar.setScrollProgress(0f);
    }

    @Override
    public List<? extends Element> contents() {
        return avatarList;
    }

    // -- Nested Types -- //

    private static class AvatarTracker extends PressableWidget {
        private final CardList parent;
        private final ContextMenu context;

        private final AvatarFileSet set;
        private final String name;
        private final String author;
        private final AvatarCardElement card;

        private final Text tooltip;

        private FiguraAvatar avatar;

        private float scale = 1f;
        public Vec2f rotationTarget = new Vec2f(0, 0);
        private Vec2f rotation = new Vec2f(0, 0);

        private float rotationMomentum = 0;

        public static final Vec3f DEFAULT_COLOR = new Vec3f(0.17f, 0.31f, 0.58f);

        private AvatarTracker(Path path, AvatarFileSet set, CardList parent) {
            super(0, 0, 64, 96, LiteralText.EMPTY);
            this.parent = parent;

            this.context = new ContextMenu(this);
            if (Math.random() < 0.001 || set.metadata.cardColor.equalsIgnoreCase("fran"))
                this.context.addAction(new LiteralText("Fran is cute :3").setStyle(ColorUtils.Colors.FRAN_PINK.style), button -> FiguraToast.sendToast("meow", "§a❤§b❤§c❤§d❤§e❤§r", FiguraToast.ToastType.CHEESE));
            this.context.addAction(new TranslatableText("figura.gui.context.card_open"), button -> {
                Path modelDir = FiguraMod.getLocalAvatarDirectory();
                Util.getOperatingSystem().open(modelDir.resolve(path).toFile());
            });

            this.set = set;
            this.name = set.metadata.avatarName;
            this.author = set.metadata.creatorName;
            this.card = new AvatarCardElement(getColor(set.metadata.cardColor), 0, Text.of(name), Text.of(author));
            this.tooltip = new LiteralText(name + "\n").append(new LiteralText(author).formatted(Formatting.DARK_PURPLE, Formatting.ITALIC));
        }

        public static Vec3f getColor(String colorName) {
            return switch (colorName.toLowerCase()) {
                case "ace" -> ColorUtils.Colors.ACE_BLUE.rgb;
                case "fran" -> ColorUtils.Colors.FRAN_PINK.rgb;
                case "lily" -> ColorUtils.Colors.LILY_RED.rgb;
                case "maya" -> ColorUtils.Colors.MAYA_BLUE.rgb;
                case "nice" -> ColorUtils.Colors.NICE.rgb;
                case "largecheese" -> Vec3f.NEGATIVE_X;
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
            //render card
            matrices.push();

            //transforms
            matrices.translate(x + 32, y + 48, 100);
            matrices.scale(scale, scale, scale);

            //animate
            animate(delta, mouseX, mouseY);

            //selected overlay
            if (this.parent.getSelectedEntry() == this) {
                matrices.push();
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-rotation.y));
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation.x + rotationMomentum));

                UIHelper.fillRounded(matrices, -33, -49, width + 2, height + 2, 0xFFFFFFFF);

                matrices.pop();
            }

            //render card
            card.entity = MinecraftClient.getInstance().player;
            card.setRotation(rotation.x + rotationMomentum, -rotation.y);
            card.render(matrices, mouseX, mouseY, delta);

            matrices.pop();
        }

        @Override
        public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
            UIHelper.renderTooltip(matrices, new LiteralText(name + "\n").append(new LiteralText(author).formatted(Formatting.DARK_PURPLE, Formatting.ITALIC)), mouseX, mouseY);
        }

        public void animate(float deltaTime, int mouseX, int mouseY) {
            rotationMomentum = (float) MathHelper.lerp((1 - Math.pow(0.8, deltaTime)), rotationMomentum, 0);

            if (this.isMouseOver(mouseX, mouseY) || this.isFocused()) {
                if (this.isFocused()) {
                    rotationTarget = new Vec2f(
                            0f,
                            0f
                    );
                } else {
                    rotationTarget = new Vec2f(
                            ((mouseX - (x + 32)) / 32f) * 30,
                            ((mouseY - (y + 48)) / 48f) * 30
                    );
                }

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
        public void onPress() {
            if (Math.abs(rotationMomentum) < 10)
                equipAvatar();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.isMouseOver(mouseX, mouseY))
                return false;

            //context menu on right click
            if (button == 1) {
                context.setPos((int) mouseX, (int) mouseY);
                context.setVisible(true);
                parent.contextMenu = context;
                return true;
            }
            //hide old context menu
            else if (parent.contextMenu == context) {
                context.setVisible(false);
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            boolean over = this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);

            if (over) {
                parent.hoverText = tooltip;
            } else if (parent.hoverText == tooltip)
                parent.hoverText = null;

            return over;
        }

        private void equipAvatar() {
            //Re-load and re-equip
            load();
            FiguraLocalDealer.localPlayerAvatarHolder.entries[0] = avatar;
            lastFileSet = set;

            //Re-load avatar so that the reference isn't kept
            load();

            rotationMomentum = rotation.x < 0 ? 360 : -360;

            parent.selectedEntry = this;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
        }
    }
}
