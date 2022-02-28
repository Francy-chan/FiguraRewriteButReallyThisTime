package net.blancworks.figura.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.FiguraMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class FiguraToast implements Toast {

    private final Text title;
    private final Text message;
    private final ToastType type;

    private long startTime;
    private boolean justUpdated;

    public FiguraToast(Text title, Text message, ToastType type) {
        this.title = title.shallowCopy().fillStyle(Style.EMPTY.withColor(type.color));
        this.message = message;
        this.type = type;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        long timeDiff = startTime - this.startTime;

        if (this.justUpdated) {
            this.startTime = startTime;
            this.justUpdated = false;
        }

        RenderSystem.setShaderTexture(0, type.texture);
        DrawableHelper.drawTexture(matrices, 0, 0, 0f, (int) (timeDiff / 208 % type.frames + 1) * 32f, getWidth(), getHeight(), 160, 32 * type.frames);

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
    public static void sendToast(Object title) {
        sendToast(title, LiteralText.EMPTY);
    }

    public static void sendToast(Object title, ToastType type) {
        sendToast(title, LiteralText.EMPTY, type);
    }

    public static void sendToast(Object title, Object message) {
        sendToast(title, message, ToastType.DEFAULT);
    }

    public static void sendToast(Object title, Object message, ToastType type) {
        Text text = title instanceof Text t ? t : new TranslatableText(title.toString());
        Text text2 = message instanceof Text m ? m : new TranslatableText(message.toString());

        if (type == ToastType.DEFAULT && (FiguraMod.CHEESE_DAY || Math.random() < 0.0001))
            type = ToastType.CHEESE;

        ToastManager toasts = MinecraftClient.getInstance().getToastManager();
        toasts.clear();
        toasts.add(new FiguraToast(text, text2, type));
    }

    public enum ToastType {
        DEFAULT(new Identifier("figura", "textures/gui/toast/default.png"), 4, 0x55FFFF),
        WARNING(new Identifier("figura", "textures/gui/toast/warning.png"), 4, 0xFFFF00),
        ERROR(new Identifier("figura", "textures/gui/toast/error.png"), 4, 0xFF0000),
        CHEESE(new Identifier("figura", "textures/gui/toast/cheese.png"), 1, 0xF8C53A);

        private final Identifier texture;
        private final int frames;
        private final int color;

        ToastType(Identifier texture, int frames, int color) {
            this.texture = texture;
            this.frames = frames;
            this.color = color;
        }
    }
}
