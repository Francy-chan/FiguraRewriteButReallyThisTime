package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class PlayerList extends Panel implements Element {

    private final HashMap<UUID, PlayerEntry> players = new HashMap<>();
    private final ArrayList<PlayerEntry> playerList = new ArrayList<>();
    private final HashSet<UUID> missingPlayers = new HashSet<>();

    private final ScrollBarWidget scrollbar;
    protected final SliderWidget parentSlider;
    protected PlayerEntry selectedEntry;

    public PlayerList(int x, int y, int width, int height, SliderWidget parentSlider) {
        super(x, y, width, height, LiteralText.EMPTY);

        //slider
        this.parentSlider = parentSlider;

        //scrollbar
        scrollbar = new ScrollBarWidget(x + width - 14, y + 4, 10, height - 8, 0f);
        addDrawableChild(scrollbar);

        //select self
        loadContents();
        PlayerEntry local = players.get(MinecraftClient.getInstance().player.getUuid());
        if (local != null) local.select();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        loadContents();

        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + 1, y + 1, width - 2, height - 2);

        int totalHeight = 48 * playerList.size();

        //scrollbar visible
        scrollbar.visible = totalHeight > height;

        //render stuff
        int xOffset = width / 2 - 87 - (scrollbar.visible ? 7 : 0);
        int playerY = scrollbar.visible ? (int) -(MathHelper.lerp(scrollbar.getScrollProgress(), -8, totalHeight - height)) : 8;

        for (PlayerEntry player : playerList) {
            player.x = x + xOffset;
            player.y = y + playerY;

            if (player.y + player.getHeight() > this.y)
                player.render(matrices, mouseX, mouseY, delta);

            if (playerY > this.x + this.height)
                break;

            playerY += 48;
        }

        //reset scissor
        RenderSystem.disableScissor();

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.scrollbar.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }

    private void loadContents() {
        //reset missing players
        missingPlayers.clear();
        missingPlayers.addAll(players.keySet());

        //for all players
        for (UUID uuid : MinecraftClient.getInstance().player.networkHandler.getPlayerUuids()) {
            //get player
            PlayerListEntry player = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(uuid);
            if (player == null)
                continue;

            //get player data
            String name = player.getProfile().getName();
            UUID id = player.getProfile().getId();
            Identifier skin = player.getSkinTexture();

            //player is not missing
            missingPlayers.remove(id);

            players.computeIfAbsent(id, uuid1 -> {
                PlayerEntry entry = new PlayerEntry(name, id, skin, this);

                playerList.add(entry);
                playerList.sort((player1, player2) -> player1.getName().compareToIgnoreCase(player2.getName()));

                addSelectableChild(entry);
                return entry;
            });
        }

        //remove missing players
        for (UUID missingID : missingPlayers) {
            PlayerEntry entry = players.remove(missingID);
            playerList.remove(entry);
            remove(entry);
        }
    }

    public PlayerEntry getSelectedEntry() {
        return selectedEntry;
    }

    public static class PlayerEntry extends ClickableWidget {
        private final PlayerList parent;

        private final String name;
        private final UUID id;
        private final Identifier skin;
        private final TrustContainer trust;

        private float scale = 1f;

        private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/player_list.png");

        public PlayerEntry(String name, UUID id, Identifier skin, PlayerList parent) {
            super(0, 0, 174, 40, LiteralText.EMPTY);
            this.name = name;
            this.id = id;
            this.skin = skin;
            this.parent = parent;
            this.trust = TrustManager.get(id);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            matrices.translate(x + 87, y + 20, 100);
            matrices.scale(scale, scale, scale);

            animate(mouseX, mouseY, delta);

            //fix x, y
            int x = -87;
            int y = -20;

            //selected overlay
            if (this.parent.getSelectedEntry() == this) {
                UIHelper.fillRounded(matrices, x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF);
            }

            //background
            RenderSystem.setShaderTexture(0, BACKGROUND);
            drawTexture(matrices, x, y, 174, 40, 0f, 0f, 174, 40, 174, 40);

            //head
            RenderSystem.setShaderTexture(0, this.skin);
            drawTexture(matrices, x + 4, y + 4, 32, 32, 8f, 8f, 8, 8, 64, 64);

            //hat
            RenderSystem.enableBlend();
            drawTexture(matrices, x + 4, y + 4, 32, 32, 40f, 8f, 8, 8, 64, 64);
            RenderSystem.disableBlend();

            //name
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            UIHelper.renderOutlineText(matrices, textRenderer, Text.of(this.name), x + 40, y + 4, 0xFFFFFF, 0);

            //uuid
            matrices.push();
            matrices.translate(x + 40, y + 4 + textRenderer.fontHeight, 0f);
            matrices.scale(0.5f, 0.5f, 0.5f);
            drawTextWithShadow(matrices, textRenderer, Text.of(this.id.toString()), 0, 0, 0x888888);
            matrices.pop();

            //trust
            drawTextWithShadow(matrices, textRenderer, trust.getGroupName(), x + 40, y + height - textRenderer.fontHeight - 4, trust.getGroupColor());

            matrices.pop();
        }

        private void animate(int mouseX, int mouseY, float delta) {
            if (this.parent.isMouseOver(mouseX, mouseY) && this.hovered || this.isFocused()) {
                scale = (float) MathHelper.lerp(1 - Math.pow(0.2, delta), scale, 1.2f);
            } else {
                scale = (float) MathHelper.lerp(1 - Math.pow(0.3, delta), scale, 1f);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button != 0 || !this.isMouseOver(mouseX, mouseY) || !this.parent.isVisible() || !this.parent.isMouseOver(mouseX, mouseY))
                return false;

            //select
            select();

            playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }

        public void select() {
            //set selected entry
            parent.selectedEntry = this;

            //set new slider action
            parent.parentSlider.setAction(scroll -> {
                //get group id
                int index = (int) (scroll.getScrollProgress() * (((SliderWidget) scroll).getSteps() - 1));
                Identifier newTrust = TrustManager.GROUPS.keySet().stream().toList().get(index);

                //set trust parent
                trust.setParent(newTrust);
            });

            //set slider progress
            parent.parentSlider.setScrollProgress(TrustManager.GROUPS.keySet().stream().toList().indexOf(trust.getParent()) / (parent.parentSlider.getSteps() - 1f));
            System.out.println((parent.parentSlider.getSteps() - 1f));
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
        }

        public String getName() {
            return name;
        }

        public UUID getId() {
            return id;
        }

        public TrustContainer getTrust() {
            return trust;
        }
    }
}
