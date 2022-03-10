package net.blancworks.figura.ui.panels;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.io.nbt.deserializers.FiguraAvatarDeserializer;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.modifications.mixins.client.gui.screen.ScreenMixin;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.widgets.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WardrobePanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/wardrobe.png");

    private CardList cardList;
    private SwitchButton expandButton;
    private StatusWidget statusWidget;

    private int cardListHeight = 0;

    private float listHeightPrecise;
    private float listYPrecise;
    private float expandYPrecise;

    public WardrobePanel() {
        super(new TranslatableText("figura.gui.panels.title.wardrobe"));
    }

    @Override
    protected void init() {
        super.init();

        // -- middle -- //

        cardListHeight = (int) (height * 0.22);

        //main entity
        int playerY = (int) (height * 0.25f);
        int entityWidth = width - 192;
        addDrawableChild(new InteractableEntity(width / 2 - entityWidth / 2, 32, entityWidth, height - cardListHeight - 64, playerY, -15f, 30f, MinecraftClient.getInstance().player));

        //card list
        cardList = new CardList(32, height - cardListHeight, width - 64, cardListHeight - 4);
        addDrawableChild(cardList);

        //expand button
        expandButton = new SwitchButton(width / 2 - 10, height - cardListHeight - 28, 20, 20, 0, 0, 20, new Identifier("figura", "textures/gui/expand.png"), 40, 40, new TranslatableText("figura.gui.wardrobe.expand_wardrobe.tooltip"), btn -> {
            boolean expanded = expandButton.isToggled();

            //hide widgets
            for (Element element : this.children()) {
                if (element instanceof ClickableWidget widget)
                    widget.visible = !expanded;
            }

            //update expand button
            expandButton.setUV(expanded ? 20 : 0, 0);
            expandButton.setTooltip(expanded ? new TranslatableText("figura.gui.wardrobe.minimize_wardrobe.tooltip") : new TranslatableText("figura.gui.wardrobe.expand_wardrobe.tooltip"));
            expandButton.visible = true;

            //card list search bar
            cardList.toggleSearchBar(expanded);
        });
        addDrawableChild(expandButton);

        // -- left side -- //

        //status widget
        statusWidget = new StatusWidget(12, 32);
        addDrawable(statusWidget);

        int buttonY = height - cardListHeight - 92;

        //upload
        addDrawableChild(new TexturedButton(12, buttonY, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/upload.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.upload.tooltip"), button -> {
            if (CardList.lastFileSet != null) {

                NbtCompound avatarCompound = CardList.lastFileSet.getAvatarNbt();

                FiguraHouse.getBackend().equipAvatar(avatarCompound, a -> {
                    FiguraLocalDealer.localPlayerAvatarHolder.entries[0] = null; //Remove local avatar, as we're now using the one on the backend.

                    //Get entity metadata from main player
                    FiguraMetadataHolder holder = (FiguraMetadataHolder) MinecraftClient.getInstance().player;
                    FiguraMetadata metadata = holder.getFiguraMetadata();

                    //Read avatar
                    FiguraAvatar avatar = FiguraAvatarDeserializer.getInstance().deserialize(avatarCompound);

                    //Set avatar from NBT commpound
                    metadata.getGroupByID(FiguraBackendDealer.ID).entries[0] = avatar;
                });
            }
        }));

        //reload
        addDrawableChild(new TexturedButton(12, buttonY += 28, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/reload.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.reload.tooltip"), button -> {
            FiguraToast.sendToast(new LiteralText("lol nope").setStyle(Style.EMPTY.withColor(0xFFADAD)), FiguraToast.ToastType.DEFAULT);
        }));

        //delete
        addDrawableChild(new TexturedButton(12, buttonY + 28, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/delete.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.delete.tooltip"), button -> FiguraHouse.getBackend().deleteAvatar(msg -> {

        })));

        // -- right side -- //

        buttonY = height - cardListHeight - 64;

        //keybinds
        TexturedButton keybinds = new TexturedButton(width - 36, buttonY, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/keybind.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.keybind.tooltip"), button -> {
            setVisible(false);
            setChildScreen(new KeybindPanel(this));
        });
        keybinds.active = false;
        addDrawableChild(keybinds);

        //sounds
        TexturedButton sounds = new TexturedButton(width - 36, buttonY + 28, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/sound.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.sound.tooltip"), button -> {
            setVisible(false);
            setChildScreen(new SoundPanel(this));
        });
        sounds.active = false;
        addDrawableChild(sounds);

        listHeightPrecise = cardList.height;
        listYPrecise = cardList.y;
        expandYPrecise = expandButton.y;
    }

    @Override
    public void tick() {
        statusWidget.tick();
        cardList.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        renderBackground();

        //expand animation
        float lerpDelta = (float) (1f - Math.pow(0.6f, delta));

        listYPrecise = MathHelper.lerp(lerpDelta, listYPrecise, expandButton.isToggled() ? 56f : height - cardListHeight);
        listHeightPrecise = MathHelper.lerp(lerpDelta, listHeightPrecise, expandButton.isToggled() ? height - 60f : cardListHeight - 4f);
        this.cardList.updateHeight((int) listYPrecise, (int) listHeightPrecise);

        expandYPrecise = MathHelper.lerp(lerpDelta, expandYPrecise, listYPrecise - 26f);
        this.expandButton.y = (int) expandYPrecise;

        //render children
        if (isVisible()) {
            super.render(matrices, mouseX, mouseY, delta);
        } else if (getChildScreen() != null) {
            getChildScreen().render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && expandButton.isToggled()) {
            expandButton.onPress();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setVisible(boolean visible) {
        if (expandButton.isToggled())
            expandButton.onPress();

        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget)
                widget.visible = visible;
        }

        for (Drawable drawable : ((ScreenMixin) (this)).getDrawables()) {
            if (drawable instanceof FiguraDrawable widget)
                widget.setVisible(visible);
        }
    }
}
