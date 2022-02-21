package net.blancworks.figura.ui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;

public class CardList extends ElementListWidget<CardList.CardEntry> {

    public CardList(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    public abstract static class CardEntry extends ElementListWidget.Entry<CardEntry> {}
}
