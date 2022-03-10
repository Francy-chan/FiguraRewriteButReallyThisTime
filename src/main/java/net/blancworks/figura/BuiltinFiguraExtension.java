package net.blancworks.figura;

import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.api.FiguraAPI;
import net.blancworks.figura.avatar.script.api.customizations.nameplate.NameplateAPI;
import net.blancworks.figura.avatar.script.api.customizations.vanillamodel.VanillaModelAPI;
import net.blancworks.figura.avatar.script.api.general.RendererAPI;
import net.blancworks.figura.avatar.script.api.general.SoundAPI;
import net.blancworks.figura.avatar.script.api.math.MatricesAPI;
import net.blancworks.figura.avatar.script.api.math.VectorsAPI;
import net.blancworks.figura.avatar.script.api.model.ModelPartAPI;
import net.blancworks.figura.avatar.script.api.pings.PingsAPI;
import net.blancworks.figura.avatar.script.api.wrappers.NBT.NbtCompoundWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.block.BlockStateWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.item.ItemStackWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.BiomeWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.WorldWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.entity.EntityWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.entity.LivingEntityWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.entity.PlayerEntityWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.entity.effect.StatusEffectInstanceWrapper;
import net.blancworks.figura.utils.external.FiguraExtension;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

        // Math
        addCustomAPI("vector", a -> new VectorsAPI());
        addCustomAPI("matrix", a -> new MatricesAPI());


        // World references
        //These APIs also need to be referenced by Figura for updating, so they push put to that value as well as to the script.
        addCustomAPI("player", a -> {
            var wrapper = new LivingEntityWrapper<PlayerEntity>();
            a.getScript().luaState.playerWrapper = wrapper;
            return wrapper;
        });

        addCustomAPI("world", a -> {
            var wrapper = new WorldWrapper();
            a.getScript().luaState.worldWrapper = wrapper;
            return wrapper;
        });

        // Customizations
        addCustomAPI("vanilla_model", VanillaModelAPI::new);
        addCustomAPI("nameplate", NameplateAPI::new);

        // Misc
        addCustomAPI("renderer", a -> new RendererAPI());
        addCustomAPI("sounds", a -> new SoundAPI());
        addCustomAPI("pings", PingsAPI::new);

    }

    public void setupWrappers() {
        // -- Wrappers -- //
        addWrapper(FiguraModelPart.class, ModelPartAPI::new);

        addWrapper(ItemStack.class, ItemStackWrapper::new);
        addWrapper(BlockState.class, BlockStateWrapper::new);

        addWrapper(World.class, WorldWrapper::new);
        addWrapper(Biome.class, BiomeWrapper::new);

        addWrapper(Entity.class, EntityWrapper::new);
        addWrapper(LivingEntity.class, LivingEntityWrapper::new);
        addWrapper(PlayerEntity.class, PlayerEntityWrapper::new);

        addWrapper(StatusEffectInstance.class, StatusEffectInstanceWrapper::new);

        addWrapper(NbtCompound.class, NbtCompoundWrapper::new);
    }
}
