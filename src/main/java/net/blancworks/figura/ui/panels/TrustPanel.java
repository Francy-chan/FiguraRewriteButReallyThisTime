package net.blancworks.figura.ui.panels;

import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TrustPanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/trust.png");

    private PlayerList playerList;
    private InteractableEntity entityWidget;

    private SliderWidget slider;

    private TrustList trustList;
    private TexturedButton expandButton;

    private float listYPrecise;
    private float expandYPrecise;

    public TrustPanel() {
        super(new TranslatableText("figura.gui.panels.title.trust"));
    }

    @Override
    protected void init() {
        super.init();

        //trust slider and list
        slider = new SliderWidget(240, 60 + height / 2, width - 252, 11, 1f, 5);
        trustList = new TrustList(240, height, width - 252, height - 76);

        // -- left -- //

        //player list
        playerList = new PlayerList(12, 32, 220, height - 44, slider, trustList); // 174 entry + 32 padding + 10 scrollbar + 4 scrollbar padding
        addDrawableChild(playerList);

        // -- right -- //

        //entity widget
        int playerY = (int) (height * 0.25f);
        entityWidget = new InteractableEntity(240, 32, width - 252, height / 2, playerY, -15f, 30f, MinecraftClient.getInstance().player);
        addDrawableChild(entityWidget);

        // -- bottom -- //

        //add slider
        addDrawableChild(slider);

        //add trust list
        addDrawableChild(trustList);

        //expand button
        expandButton = new TexturedButton(230 + (width - 252) / 2, height - 32, 20, 20, 0, 0, 20, new Identifier("figura", "textures/gui/expand.png"), 40, 40, new TranslatableText("figura.gui.trust.expand_trust.tooltip"), btn -> toggleExpand(!expandButton.isToggled()));
        addDrawableChild(expandButton);

        listYPrecise = trustList.y;
        expandYPrecise = expandButton.y;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        renderBackground();

        //set entity to render
        PlayerList.PlayerEntry entity = playerList.getSelectedEntry();
        if (entity != null) {
            PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(entity.getId());
            entityWidget.setEntity(player);
        }

        //expand animation
        float lerpDelta = (float) (1f - Math.pow(0.6f, delta));

        listYPrecise = MathHelper.lerp(lerpDelta, listYPrecise, expandButton.isToggled() ? 64f : height);
        this.trustList.y = (int) listYPrecise;

        expandYPrecise = MathHelper.lerp(lerpDelta, expandYPrecise, listYPrecise - 32f);
        this.expandButton.y = (int) expandYPrecise;

        if (slider.visible) {
            TrustContainer selectedTrust = playerList.getSelectedEntry().getTrust();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            MutableText text = selectedTrust.getGroupName();
            UIHelper.renderOutlineText(matrices, textRenderer, text, slider.x + slider.getWidth() / 2f - textRenderer.getWidth(text) / 2f, slider.y - 4 - textRenderer.fontHeight, selectedTrust.getGroupColor(), 0x202020);
        }

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
        TrustManager.saveToDisk();
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }

    private void toggleExpand(boolean expanded) {
        //toggle
        expandButton.setToggled(expanded);

        //hide widgets
        entityWidget.visible = !expanded;
        slider.visible = !expanded;

        //update expand button
        expandButton.setUV(expanded ? 20 : 0, 0);
        expandButton.setTooltip(expanded ? new TranslatableText("figura.gui.trust.minimize_trust.tooltip") : new TranslatableText("figura.gui.trust.expand_trust.tooltip"));
    }
}
