package net.blancworks.figura.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.FiguraMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class FiguraToast implements Toast {

    private final Text title;
    private final Text message;
    private final boolean cheese;

    private long startTime;
    private boolean justUpdated;

    private static final Identifier TEXTURE = new Identifier("figura", "textures/gui/toast.png");

    public FiguraToast(Text title, Text message) {
        this.cheese = FiguraMod.CHEESE_DAY || Math.random() < 0.0001;
        this.title = title.shallowCopy().fillStyle(Style.EMPTY.withColor(cheese ? 0xF8C53A : 0x55FFFF));
        this.message = message;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        long timeDiff = startTime - this.startTime;

        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        RenderSystem.setShaderTexture(0, TEXTURE);
        DrawableHelper.drawTexture(matrices, 0, 0, 0f, cheese ? 0 : (int) (timeDiff / 208 % 4 + 1) * 32f, 160, 32, 160, 160);

        TextRenderer renderer = manager.getClient().textRenderer;
        if (this.message == null) {
            renderer.draw(matrices, this.title, 31f, 12f, 0xFFFFFF);
        } else {
            renderer.draw(matrices, this.title, 31f, 7f, 0xFFFFFF);
            renderer.draw(matrices, this.message, 31f, 18f, 0xFFFFFF);
        }

        return timeDiff < 5000 ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    //new toast
    public static void sendToast(Object title, Object message) {
        Text text = title instanceof Text t ? t : new TranslatableText(title.toString());
        Text text2 = message instanceof Text m ? m : new TranslatableText(message.toString());

        ToastManager toasts = MinecraftClient.getInstance().getToastManager();
        toasts.clear();
        toasts.add(new FiguraToast(text, text2));
    }
}
