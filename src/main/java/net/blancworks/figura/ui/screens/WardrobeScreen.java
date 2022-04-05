package net.blancworks.figura.ui.screens;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.io.nbt.deserializers.FiguraAvatarDeserializer;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.widgets.*;
import net.blancworks.figura.ui.widgets.lists.CardList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WardrobeScreen extends AbstractPanelScreen {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/wardrobe.png");
    public static final Text TITLE = new TranslatableText("figura.gui.panels.title.wardrobe");

    // -- widgets -- //
    private CardList cardList;
    private SwitchButton expandButton;
    private StatusWidget statusWidget;

    // -- widget logic -- //
    private int cardListHeight = 0;
    private float listHeightPrecise;
    private float listYPrecise;
    private float expandYPrecise;

    public WardrobeScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 2);
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

        //expand button
        expandButton = new SwitchButton(width / 2 - 10, height - cardListHeight - 28, 20, 20, 0, 0, 20, new Identifier("figura", "textures/gui/expand.png"), 40, 40, new TranslatableText("figura.gui.wardrobe.expand_wardrobe.tooltip"), btn -> {
            boolean expanded = expandButton.isToggled();

            //hide widgets
            for (Element element : this.children()) {
                if (element instanceof ClickableWidget widget && widget != this.backButton && widget != this.helpButton)
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

        //card list
        cardList = new CardList(32, height - cardListHeight, width - 64, cardListHeight - 4);
        addDrawableChild(cardList);

        // -- left side -- //

        //status widget
        statusWidget = new StatusWidget(12, 32);
        addDrawable(statusWidget);

        int buttonY = height - cardListHeight - 92;

        //upload
        addDrawableChild(new TexturedButton(4, buttonY, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/upload.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.upload.tooltip"), button -> {
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
        addDrawableChild(new TexturedButton(4, buttonY += 28, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/reload.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.reload.tooltip"), button -> {
            FiguraToast.sendToast(new LiteralText("lol nope").setStyle(Style.EMPTY.withColor(0xFFADAD)), FiguraToast.ToastType.DEFAULT);
        }));

        //delete
        addDrawableChild(new TexturedButton(4, buttonY + 28, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/delete.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.delete.tooltip"), button -> FiguraHouse.getBackend().deleteAvatar(msg -> {

        })));

        // -- right side -- //

        buttonY = height - cardListHeight - 64;

        //keybinds
        TexturedButton keybinds = new TexturedButton(width - 28, buttonY, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/keybind.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.keybind.tooltip"), button -> {
            MinecraftClient.getInstance().setScreen(new KeybindScreen(this));
        });
        keybinds.active = false; //TODO
        addDrawableChild(keybinds);

        //sounds
        TexturedButton sounds = new TexturedButton(width - 28, buttonY + 28, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/sound.png"), 48, 48, new TranslatableText("figura.gui.wardrobe.sound.tooltip"), button -> {
            MinecraftClient.getInstance().setScreen(new SoundScreen(this));
        });
        sounds.active = false; //TODO
        addDrawableChild(sounds);

        listHeightPrecise = cardList.height;
        listYPrecise = cardList.y;
        expandYPrecise = expandButton.y;
    }

    @Override
    public void tick() {
        statusWidget.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //expand animation
        float lerpDelta = (float) (1f - Math.pow(0.6f, delta));

        listYPrecise = MathHelper.lerp(lerpDelta, listYPrecise, expandButton.isToggled() ? 56f : height - cardListHeight);
        listHeightPrecise = MathHelper.lerp(lerpDelta, listHeightPrecise, expandButton.isToggled() ? height - 60f : cardListHeight - 4f);
        this.cardList.updateHeight((int) listYPrecise, (int) listHeightPrecise);

        expandYPrecise = MathHelper.lerp(lerpDelta, expandYPrecise, listYPrecise - 26f);
        this.expandButton.y = (int) expandYPrecise;

        //render
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        //yeet ESC key press for collapsing the card list
        if (keyCode == 256 && expandButton.isToggled()) {
            expandButton.onPress();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.cardList.contextMenuClick(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }
}
