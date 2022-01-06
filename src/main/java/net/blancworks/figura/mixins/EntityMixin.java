package net.blancworks.figura.mixins;

import net.blancworks.figura.accessors.FiguraMetadataHolder;
import net.blancworks.figura.dealer.FiguraDealer;
import net.blancworks.figura.dealer.FiguraHouse;
import net.blancworks.figura.entity.FiguraEntityMetadata;
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

    @Shadow public abstract String getUuidAsString();

    @Unique
    private FiguraEntityMetadata figuraMetadata;

    @Inject(at = @At("HEAD"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
        getFiguraMetadata();
    }

    public FiguraEntityMetadata getFiguraMetadata() {
        //Create metadata, if none is found.
        if (figuraMetadata == null) {
            System.out.println("MAKING FIGURA METADATA FOR " + getUuidAsString());
            figuraMetadata = FiguraHouse.createEntityMetadata((Entity) ((Object) this));
        }

        //Return metadata.
        return figuraMetadata;
    }
}
