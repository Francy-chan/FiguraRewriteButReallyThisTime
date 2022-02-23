package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.cards.CardEntity;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.CardList;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
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

        cardList = new CardList(32, height - 64, width - 64, height - 64 - 4);

        addDrawableChild(new TexturedButton(32, height - 64 - 32, 60, 20, new TranslatableText("Upload"), button -> {
            if(CardList.lastFileSet != null) {
                NbtCompound avatarCompound = new NbtCompound();
                CardList.lastFileSet.writeAvatarNBT(avatarCompound);

                FiguraHouse.getBackend().uploadAvatar(avatarCompound, a -> {
                    FiguraLocalDealer.localPlayerAvatarGroup.avatars[0] = null; //Remove local avatar, as we're now using the one on the backend.

                    //Get entity metadata from main player
                    FiguraMetadataHolder holder = (FiguraMetadataHolder) MinecraftClient.getInstance().player;
                    FiguraEntityMetadata metadata = holder.getFiguraMetadata();

                    //Read avatar
                    FiguraAvatar avatar = new FiguraAvatar();
                    FiguraAvatarNbtConverter.readNBT(avatar, avatarCompound);

                    //Set avatar from NBT commpound
                    metadata.getGroupByID(FiguraBackendDealer.ID).avatars[0] = avatar;
                });
            }
        }));
        addDrawableChild(cardList);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, BACKGROUND);

        matrices.push();
        matrices.translate(screen.x / 2f, screen.y / 2f, 0f);

        //player
        UIHelper.drawEntity(0, 0, 50, 0f, 45f, MinecraftClient.getInstance().player, matrices);


        matrices.pop();

        super.render(matrices, mouseX, mouseY, delta);
    }
}
