package net.blancworks.figura.ui.panels;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.io.nbt.deserializers.FiguraAvatarDeserializer;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.CardList;
import net.blancworks.figura.ui.widgets.InteractableEntity;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class WardrobePanel extends Panel {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/wardrobe.png");
    private CardList cardList;
    private TexturedButton expandButton;
    private boolean isExpanded = false;
    private int cardListSize = 0;

    public WardrobePanel() {
        super(new TranslatableText("figura.gui.panels.title.wardrobe"));
    }

    @Override
    protected void init() {
        super.init();

        cardListSize = (int) (height * 0.22);

        //main entity
        int playerY = (int) (height * 0.25f);
        addDrawableChild(new InteractableEntity(48, 32, width - 96, height - cardListSize - 64, playerY, -15f, 30f, MinecraftClient.getInstance().player));

        //card list
        cardList = new CardList(32, height - cardListSize, width - 64, cardListSize - 4);
        addDrawableChild(cardList);

        //expand button
        expandButton = new TexturedButton(width / 2 - 10, height - cardListSize - 28, 20, 20, 0, 0, 20, new Identifier("figura", "textures/gui/extend_icon.png"), 40, 40, btn -> toggleExpand());
        addDrawableChild(expandButton);

        //upload
        int buttonY = height - cardListSize - 136;
        addDrawableChild(new TexturedButton(12, buttonY += 32, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/upload.png"), 48, 48, button -> {
            if (CardList.lastFileSet != null) {

                NbtCompound avatarCompound = CardList.lastFileSet.getAvatarNbt();

                FiguraHouse.getBackend().uploadAvatar(avatarCompound, a -> {
                    FiguraLocalDealer.localPlayerAvatarHolder.avatars[0] = null; //Remove local avatar, as we're now using the one on the backend.

                    //Get entity metadata from main player
                    FiguraMetadataHolder holder = (FiguraMetadataHolder) MinecraftClient.getInstance().player;
                    FiguraEntityMetadata metadata = holder.getFiguraMetadata();

                    //Read avatar
                    FiguraAvatar avatar = FiguraAvatarDeserializer.getInstance().deserialize(avatarCompound);

                    //Set avatar from NBT commpound
                    metadata.getGroupByID(FiguraBackendDealer.ID).avatars[0] = avatar;
                });
            }
        }));

        //reload
        addDrawableChild(new TexturedButton(12, buttonY += 32, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/reload.png"), 48, 48, button -> {}));

        //delete
        addDrawableChild(new TexturedButton(12, buttonY + 32, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/delete.png"), 48, 48, button -> FiguraHouse.getBackend().deleteAvatar(msg -> {

        })));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, BACKGROUND);

        //expand animation
        float lerpDelta = delta * 0.6f;

        int listY = (int) MathHelper.lerp(lerpDelta, this.cardList.y, isExpanded ? 56f : height - cardListSize);
        int listHeight = (int) MathHelper.lerp(lerpDelta, this.cardList.height, isExpanded ? height - 60f : cardListSize - 4f);
        this.cardList.updateHeight(listY, listHeight);

        this.expandButton.y = (int) MathHelper.lerp(lerpDelta, this.expandButton.y, this.cardList.y - 18);

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        //yeet mouse 0 and isDragging check
        return this.getFocused() != null && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    private void toggleExpand() {
        //toggle
        isExpanded = !isExpanded;

        //hide widgets
        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget)
                widget.visible = !isExpanded;
        }

        //update expand button
        expandButton.setUV(isExpanded ? 20 : 0, 0);
        expandButton.visible = true;
    }
}
