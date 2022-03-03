package net.blancworks.figura;

import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.api.FiguraAPI;
import net.blancworks.figura.avatar.script.api.math.MatricesAPI;
import net.blancworks.figura.avatar.script.api.math.VectorsAPI;
import net.blancworks.figura.avatar.script.api.models.ModelPartAPI;
import net.blancworks.figura.avatar.script.api.wrappers.block.BlockStateWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.item.ItemStackWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.BiomeWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.WorldWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.entity.LivingEntityWrapper;
import net.blancworks.figura.external.FiguraExtension;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

/**
 * Built-in extension used by Figura to add the APIs and such.
 * Can also be used as a nice example for how to use the FiguraExtensions class.
 */
public class BuiltinFiguraExtension extends FiguraExtension {

    public BuiltinFiguraExtension() {
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        setupAPIs();
        setupWrappers();
    }

    public void setupAPIs() {
        addCustomAPI("figura", FiguraAPI::new);
        addCustomAPI("vector", a -> new VectorsAPI());
        addCustomAPI("matrix", a -> new MatricesAPI());
    }

    public void setupWrappers() {
        // -- Wrappers -- //
        addWrapper(FiguraModelPart.class, ModelPartAPI::new);

        addWrapper(ItemStack.class, ItemStackWrapper::new);
        addWrapper(BlockState.class, BlockStateWrapper::new);

        addWrapper(World.class, WorldWrapper::new);
        addWrapper(Biome.class, BiomeWrapper::new);

        addWrapper(LivingEntity.class, LivingEntityWrapper::new);
    }
}
