package net.blancworks.figura.mixins;

import net.blancworks.figura.accessors.FiguraMetadataHolder;
import net.blancworks.figura.dealer.FiguraHouse;
import net.blancworks.figura.entity.FiguraEntityMetadata;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements FiguraMetadataHolder {

    @Unique
    private FiguraEntityMetadata figuraMetadata;

    @Inject(at = @At("HEAD"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
        getFiguraMetadata().onTick((Entity) ((Object) this));
    }

    public FiguraEntityMetadata getFiguraMetadata() {
        //Create metadata, if none is found.
        if (figuraMetadata == null)
            figuraMetadata = FiguraHouse.createEntityMetadata((Entity) ((Object) this));

        //Return metadata.
        return figuraMetadata;
    }
}
