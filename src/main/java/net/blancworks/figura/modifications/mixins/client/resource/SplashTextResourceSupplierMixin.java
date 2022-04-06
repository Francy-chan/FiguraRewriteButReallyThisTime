package net.blancworks.figura.modifications.mixins.client.resource;

import net.blancworks.figura.FiguraMod;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Calendar;
import java.util.Date;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {

    @Inject(at = @At("HEAD"), method = "get", cancellable = true)
    public void init(CallbackInfoReturnable<String> cir) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (FiguraMod.CHEESE_DAY) {
            cir.setReturnValue("LARGECHEESE!");
        } else { //b-days!!
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String bday = "Happy birthday ";

            switch (month) {
                case 1 -> {
                    if (day == 1) cir.setReturnValue(bday + "Lily!");
                }
                case 3 -> {
                    switch (day) {
                        case 5 -> cir.setReturnValue(bday + "devnull!");
                        case 7 -> cir.setReturnValue(bday + "omoflop!");
                        case 24 -> cir.setReturnValue(bday + "Figura!");
                    }
                }
                case 9 -> {
                    if (day == 21) cir.setReturnValue(bday + "Fran!");
                }
            }
        }
    }
}
