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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

public class WardrobePanel extends Panel {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/wardrobe.png");

    public CardList cardList;

    public WardrobePanel() {
        super(new TranslatableText("figura.gui.panels.title.wardrobe"));
    }

    @Override
    protected void init() {
        super.init();

        int size = (int) (height * 0.22);
        cardList = new CardList(32, height - size, width - 64, size, height - 68);

        int playerY = (int) (height * 0.25f);
        addDrawableChild(new InteractableEntity((int) (width * 0.5f), (int) (height * 0.5f - height * 0.11f), width, height, playerY, -15f, 30f, MinecraftClient.getInstance().player));

        addDrawableChild(new TexturedButton(8, height - size - 34, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/upload.png"), 48, 48, button -> {
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

        addDrawableChild(new TexturedButton(8, height - size - 60, 24, 24, 24, 0, 24, new Identifier("figura", "textures/gui/delete.png"), 48, 48, button -> FiguraHouse.getBackend().deleteAvatar(msg -> {

        })));

        addDrawableChild(cardList);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, BACKGROUND);

        if (cardList.isExpanded()) {
            //render only list
            cardList.render(matrices, mouseX, mouseY, delta);
        } else {
            //render children
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
