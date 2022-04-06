package net.blancworks.figura.ui.screens;

import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.*;
import net.blancworks.figura.ui.widgets.lists.PlayerList;
import net.blancworks.figura.ui.widgets.lists.TrustList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class TrustScreen extends AbstractPanelScreen {

    public static final Text TITLE = new TranslatableText("figura.gui.panels.title.trust");

    // -- widgets -- //
    private PlayerList playerList;
    private InteractableEntity entityWidget;

    private SliderWidget slider;

    private TrustList trustList;
    private SwitchButton expandButton;

    private TexturedButton resetButton;

    // -- widget logic -- //
    private float listYPrecise;
    private float expandYPrecise;
    private float resetYPrecise;

    public TrustScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 3);
    }

    @Override
    protected void init() {
        super.init();

        //sizes
        int listSize = Math.min(240, width / 2);

        //trust slider and list
        int fontHeight =  MinecraftClient.getInstance().textRenderer.fontHeight;
        slider = new SliderWidget(listSize + 4, (int) (height - 43 - fontHeight * 1.5), width - listSize - 8, 11, 1f, 5) {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                super.renderButton(matrices, mouseX, mouseY, delta);

                TrustContainer selectedTrust = playerList.getSelectedEntry().getTrust();
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                MutableText text = selectedTrust.getGroupName();

                matrices.push();
                matrices.translate(this.x + this.getWidth() / 2f - textRenderer.getWidth(text) * 0.75, this.y - 4 - textRenderer.fontHeight * 2, 0f);
                matrices.scale(1.5f, 1.5f, 1f);
                UIHelper.renderOutlineText(matrices, textRenderer, text, 0, 0, selectedTrust.getGroupColor(), 0x202020);
                matrices.pop();
            }
        };
        trustList = new TrustList(listSize + 4, height, width - listSize - 8, height - 68);

        // -- left -- //

        //player list
        playerList = new PlayerList(4, 32, listSize - 4, height - 36, this); // 174 entry + 32 padding + 10 scrollbar + 4 scrollbar padding
        addDrawableChild(playerList);

        // -- right -- //

        //entity widget
        int playerY = (int) (height * 0.25f);
        entityWidget = new InteractableEntity(listSize + 4, 32, width - listSize - 8, height - 47 - (height - slider.y) - textRenderer.fontHeight * 2, playerY, -15f, 30f, MinecraftClient.getInstance().player);
        addDrawableChild(entityWidget);

        // -- bottom -- //

        //add slider
        addDrawableChild(slider);

        //expand button
        expandButton = new SwitchButton(slider.x + slider.getWidth() / 2 - 10, height - 32, 20, 20, 0, 0, 20, new Identifier("figura", "textures/gui/expand.png"), 40, 40, new TranslatableText("figura.gui.trust.expand_trust.tooltip"), btn -> {
            boolean expanded = expandButton.isToggled();

            //hide widgets
            entityWidget.visible = !expanded;
            slider.visible = !expanded;

            //update expand button
            expandButton.setUV(expanded ? 20 : 0, 0);
            expandButton.setTooltip(expanded ? new TranslatableText("figura.gui.trust.minimize_trust.tooltip") : new TranslatableText("figura.gui.trust.expand_trust.tooltip"));

            //set reset button activeness
            resetButton.active = expanded;
        });
        addDrawableChild(expandButton);

        //reset all button
        resetButton = new TexturedButton(listSize + 4, height, 60, 20, new TranslatableText("figura.gui.trust.reset"), null, btn -> {
            //clear trust
            TrustContainer trust = playerList.getSelectedEntry().getTrust();
            trust.getSettings().clear();
            updateTrustData(trust);
        }) {
            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
                UIHelper.renderSliced(matrixStack, x, y, width, height, UIHelper.OUTLINE);
                super.renderButton(matrixStack, mouseX, mouseY, delta);
            }
        };
        addDrawableChild(resetButton);

        //add trust list
        addDrawableChild(trustList);

        listYPrecise = trustList.y;
        expandYPrecise = expandButton.y;
        resetYPrecise = resetButton.y;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
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

        resetYPrecise = MathHelper.lerp(lerpDelta, resetYPrecise, expandButton.isToggled() ? 42f : height);
        this.resetButton.y = (int) resetYPrecise;

        //render
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void removed() {
        TrustManager.saveToDisk();
        super.removed();
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
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount) || (slider.visible && slider.mouseScrolled(mouseX, mouseY, amount));
    }

    public void updateTrustData(TrustContainer trust) {
        //reset run action
        slider.setAction(null);

        ArrayList<Identifier> groupList = new ArrayList<>(TrustManager.GROUPS.keySet());

        //set step sizes
        slider.setSteps(TrustManager.isLocal(trust) ? groupList.size() : groupList.size() - 1);

        //set slider progress
        slider.setScrollProgress(groupList.indexOf(trust.getParent()) / (slider.getSteps() - 1f));

        //set new slider action
        slider.setAction(scroll -> {
            //set new trust parent
            Identifier newTrust = groupList.get(((SliderWidget) scroll).getStepValue());
            trust.setParent(newTrust);

            //and update the advanced trust
            trustList.updateList(trust);
        });

        //update advanced trust list
        trustList.updateList(trust);
    }
}
