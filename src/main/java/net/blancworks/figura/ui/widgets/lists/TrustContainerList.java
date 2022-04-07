package net.blancworks.figura.ui.widgets.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.screens.TrustScreen;
import net.blancworks.figura.ui.widgets.TextField;
import net.blancworks.figura.ui.widgets.trust.AbstractContainerElement;
import net.blancworks.figura.ui.widgets.trust.GroupElement;
import net.blancworks.figura.ui.widgets.trust.PlayerElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class TrustContainerList extends AbstractList {

    private final HashMap<UUID, PlayerElement> players = new HashMap<>();
    private final HashSet<UUID> missingPlayers = new HashSet<>();

    private final ArrayList<AbstractContainerElement> trustList = new ArrayList<>();

    public final TrustScreen parent;

    private int totalHeight = 0;
    public AbstractContainerElement selectedEntry;
    private String filter = "";

    public TrustContainerList(int x, int y, int width, int height, TrustScreen parent) {
        super(x, y, width, height);
        updateScissors(1, 26, -2, -27);

        this.parent = parent;

        //fix scrollbar y and height
        scrollBar.y = y + 30;
        scrollBar.setHeight(height - 34);

        //search bar
        children.add(new TextField(x + 4, y + 4, width - 8, 22, new TranslatableText("figura.gui.search"), s -> filter = s));

        //initial load
        loadGroups();
        loadPlayers();

        //select self
        PlayerElement local = players.get(MinecraftClient.getInstance().player.getUuid());
        if (local != null) local.onPress();
    }

    @Override
    public void tick() {
        //update players
        loadPlayers();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + scissorsX, y + scissorsY, width + scissorsWidth, height + scissorsHeight);

        totalHeight = 0;
        for (AbstractContainerElement trustEntry : trustList) {
            if (trustEntry.isVisible())
                totalHeight += trustEntry.getHeight() + 8;
        }

        //scrollbar visible
        scrollBar.visible = totalHeight > height - 34;
        scrollBar.setScrollRatio((float) totalHeight / trustList.size(), totalHeight - (height - 34));

        //render stuff
        int xOffset = width / 2 - 87 - (scrollBar.visible ? 7 : 0);
        int playerY = scrollBar.visible ? (int) -(MathHelper.lerp(scrollBar.getScrollProgress(), -34, totalHeight - height)) : 34;
        boolean hidden = false;

        for (AbstractContainerElement trust : trustList) {
            if (hidden || !trust.isVisible()) {
                trust.visible = false;
                continue;
            }

            trust.visible = true;
            trust.x = x + (trust instanceof PlayerElement ? Math.max(4, xOffset) : xOffset);
            trust.y = y + playerY;

            if (trust.y + trust.getHeight() > y + scissorsY)
                trust.render(matrices, mouseX, mouseY, delta);

            playerY += trust.getHeight() + 8;
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
        return trustList;
    }

    private void loadGroups() {
        for (TrustContainer container : TrustManager.GROUPS.values()) {
            GroupElement group = new GroupElement(container, this);
            trustList.add(group);
            children.add(group);
        }
    }

    private void loadPlayers() {
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
                PlayerElement entry = new PlayerElement(name, TrustManager.get(id), skin, this);

                trustList.add(entry);
                children.add(entry);

                return entry;
            });
        }

        //remove missing players
        for (UUID missingID : missingPlayers) {
            PlayerElement entry = players.remove(missingID);
            trustList.remove(entry);
            children.remove(entry);
        }

        sortList();
    }

    private void sortList() {
        trustList.sort(Comparator.naturalOrder());
        children.sort((element1, element2) -> {
            if (element1 instanceof AbstractContainerElement container1 && element2 instanceof AbstractContainerElement container2)
                return container1.compareTo(container2);
            return 0;
        });
    }

    public void updateScroll() {
        //store old scroll pos
        float pastScroll = (totalHeight - height) * scrollBar.getScrollProgress();

        //get new height
        totalHeight = 0;
        for (AbstractContainerElement trustEntry : trustList) {
            if (trustEntry.isVisible())
                totalHeight += trustEntry.getHeight() + 8;
        }

        //set new scroll percentage
        scrollBar.setScrollProgress(pastScroll / (totalHeight - height));
    }

    public AbstractContainerElement getSelectedEntry() {
        return selectedEntry;
    }
}
