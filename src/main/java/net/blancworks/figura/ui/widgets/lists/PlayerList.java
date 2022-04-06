package net.blancworks.figura.ui.widgets.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.screens.TrustScreen;
import net.blancworks.figura.ui.widgets.ContextMenu;
import net.blancworks.figura.ui.widgets.TextField;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class PlayerList extends AbstractList {

    private final HashMap<UUID, PlayerEntry> players = new HashMap<>();
    private final HashSet<UUID> missingPlayers = new HashSet<>();

    private final ArrayList<PlayerEntry> playerList = new ArrayList<>();

    private final TrustScreen parent;

    protected PlayerEntry selectedEntry;
    private String filter = "";

    public PlayerList(int x, int y, int width, int height, TrustScreen parent) {
        super(x, y, width, height);
        updateScissors(1, 26, -2, -27);

        this.parent = parent;

        //fix scrollbar y and height
        scrollBar.y = y + 30;
        scrollBar.setHeight(height - 34);

        //search bar
        children.add(new TextField(x + 4, y + 4, width - 8, 22, new TranslatableText("figura.gui.search"), s -> filter = s));

        //select self
        loadContents();
        PlayerEntry local = players.get(MinecraftClient.getInstance().player.getUuid());
        if (local != null) local.onPress();
    }

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

        int totalHeight = 48 * playerList.size();

        //scrollbar visible
        scrollBar.visible = totalHeight > height;
        scrollBar.setScrollRatio(48, totalHeight - height);

        //render stuff
        int xOffset = Math.max(4, width / 2 - 87 - (scrollBar.visible ? 7 : 0));
        int playerY = scrollBar.visible ? (int) -(MathHelper.lerp(scrollBar.getScrollProgress(), -34, totalHeight - height)) : 34;
        boolean hidden = false;

        for (PlayerEntry player : playerList) {
            if (hidden) {
                player.visible = false;
                continue;
            }

            player.visible = true;
            player.x = x + xOffset;
            player.y = y + playerY;

            if (player.y + player.getHeight() > y + scissorsY)
                player.render(matrices, mouseX, mouseY, delta);

            playerY += 48;
            if (playerY > height)
                hidden = true;
        }

        //reset scissor
        RenderSystem.disableScissor();

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends Element> contents() {
        return playerList;
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

            //filter check
            if (!name.toLowerCase().contains(filter.toLowerCase()))
                continue;

            //player is not missing
            missingPlayers.remove(id);

            players.computeIfAbsent(id, uuid1 -> {
                PlayerEntry entry = new PlayerEntry(name, id, skin, this);

                playerList.add(entry);
                children.add(entry);

                return entry;
            });
        }

        //sort list
        playerList.sort((player1, player2) -> player1.getName().compareToIgnoreCase(player2.getName()));
        children.sort((children1, children2) -> {
            if (children1 instanceof PlayerEntry player1 && children2 instanceof PlayerEntry player2)
                return player1.getName().compareToIgnoreCase(player2.getName());
            return 0;
        });

        //remove missing players
        for (UUID missingID : missingPlayers) {
            PlayerEntry entry = players.remove(missingID);
            playerList.remove(entry);
            children.remove(entry);
        }
    }

    public PlayerEntry getSelectedEntry() {
        return selectedEntry;
    }

    public static class PlayerEntry extends PressableWidget {
        private final PlayerList parent;

        private final String name;
        private final UUID id;
        private final Identifier skin;
        private final TrustContainer trust;
        private final ContextMenu context;

        private float scale = 1f;

        private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/player_list.png");

        public PlayerEntry(String name, UUID id, Identifier skin, PlayerList parent) {
            super(0, 0, 174, 40, LiteralText.EMPTY);
            this.name = name;
            this.id = id;
            this.skin = skin;
            this.parent = parent;
            this.trust = TrustManager.get(id);
            this.context = new ContextMenu(this);

            generateContext();
        }

        private void generateContext() {
            //header
            context.addDivisor(new TranslatableText("figura.gui.set_trust"));

            //actions
            ArrayList<Identifier> groupList = new ArrayList<>(TrustManager.GROUPS.keySet());
            for (int i = 0; i < (TrustManager.isLocal(trust) ? groupList.size() : groupList.size() - 1); i++) {
                Identifier parentID = groupList.get(i);
                TrustContainer container = TrustManager.get(parentID);
                context.addAction(container.getGroupName().copy().setStyle(Style.EMPTY.withColor(container.getGroupColor())), button -> {
                    trust.setParent(parentID);
                    if (parent.getSelectedEntry() == this)
                        parent.parent.updateTrustData(trust);
                });
            }
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
            if (this.isMouseOver(mouseX, mouseY) || this.isFocused()) {
                scale = (float) MathHelper.lerp(1 - Math.pow(0.2, delta), scale, 1.2f);
            } else {
                scale = (float) MathHelper.lerp(1 - Math.pow(0.3, delta), scale, 1f);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.isMouseOver(mouseX, mouseY))
                return false;

            //context menu on right click
            if (button == 1) {
                context.setPos((int) mouseX, (int) mouseY);
                context.setVisible(true);
                UIHelper.setContext(context);
                return true;
            }
            //hide old context menu
            else if (UIHelper.getContext() == context) {
                context.setVisible(false);
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);
        }

        @Override
        public void onPress() {
            //set selected entry
            parent.selectedEntry = this;

            //update trust widgets
            parent.parent.updateTrustData(this.trust);
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
