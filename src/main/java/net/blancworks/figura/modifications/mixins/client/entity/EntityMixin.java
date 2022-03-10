package net.blancworks.figura.modifications.mixins.client.entity;

import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin implements FiguraMetadataHolder {

    @Shadow public abstract UUID getUuid();

    @Unique
    private FiguraMetadata figuraMetadata;

    @Inject(at = @At("HEAD"), method = "baseTick()V")
    public void fBaseTick(CallbackInfo ci) {
        getFiguraMetadata().tick();
    }

    public FiguraMetadata getFiguraMetadata() {
        //Create metadata, if none is found.
        if (figuraMetadata == null)
            figuraMetadata = FiguraHouse.getMetadata((Entity)(Object)this);

        //Return metadata.
        return figuraMetadata;
    }
}
