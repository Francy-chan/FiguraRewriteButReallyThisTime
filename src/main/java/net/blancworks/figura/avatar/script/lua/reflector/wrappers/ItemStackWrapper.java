package net.blancworks.figura.avatar.script.lua.reflector.wrappers;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public class ItemStackWrapper extends ObjectWrapper<ItemStack> {

    // -- Functions -- //

    @LuaWhitelist
    public int getCount() {
        return target.getCount();
    }

    @LuaWhitelist
    public int getDamage() {
        return target.getDamage();
    }

    @LuaWhitelist
    public boolean isEmpty() {
        return target.isEmpty();
    }

    @LuaWhitelist
    public float getMiningSpeedMultiplier(BlockState state) {
        return target.getMiningSpeedMultiplier(state);
    }

    @LuaWhitelist
    public boolean isStackable() {
        return target.isStackable();
    }

    @LuaWhitelist
    public boolean isDamageable() {
        return target.isDamageable();
    }

    @LuaWhitelist
    public boolean isDamaged() {
        return target.isDamaged();
    }

    @LuaWhitelist
    public boolean isSuitableFor(BlockState state) {
        return target.isSuitableFor(state);
    }

    @LuaWhitelist
    public String getName() {
        return target.getName().asString();
    }

    @LuaWhitelist
    public boolean hasGlint() {
        return target.hasGlint();
    }

    @LuaWhitelist
    public String getRarity() {
        return target.getRarity().name();
    }

    @LuaWhitelist
    public int getRepairCost() {
        return target.getRepairCost();
    }

    @LuaWhitelist
    public boolean isFood() {
        return target.isFood();
    }

}
