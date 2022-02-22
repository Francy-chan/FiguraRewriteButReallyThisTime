package net.blancworks.figura.modifications.mixins;

import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
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

    @Inject(at = @At("HEAD"), method = "baseTick()V")
    public void fBaseTick(CallbackInfo ci) {
        getFiguraMetadata().tick();
    }

    public FiguraEntityMetadata getFiguraMetadata() {
        //Create metadata, if none is found.
        if (figuraMetadata == null)
            figuraMetadata = FiguraHouse.getEntityMetadata((Entity) ((Object) this));

        //Return metadata.
        return figuraMetadata;
    }
}
