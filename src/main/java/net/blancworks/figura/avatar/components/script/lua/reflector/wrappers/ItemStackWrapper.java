package net.blancworks.figura.avatar.components.script.lua.reflector.wrappers;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.minecraft.item.ItemStack;

public class ItemStackWrapper extends ObjectWrapper<ItemStack> {

    // -- Functions -- //

    @LuaWhitelist
    public int getCount(){
        return target.getCount();
    }
}
