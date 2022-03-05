package net.blancworks.figura.avatar.script.api.wrappers.world.entity;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LivingEntityWrapper<T extends LivingEntity> extends EntityWrapper<T> {


    // Transforms //
    @LuaWhitelist
    public float getBodyYaw() {
        return getBodyYaw(1);
    }

    @LuaWhitelist
    public float getBodyYaw(float delta) {
        return MathHelper.lerp(delta, target.prevBodyYaw, target.bodyYaw);
    }

    // Meta //


    // State //

    @LuaWhitelist
    public float getHealth() {
        return target.getHealth();
    }

    @LuaWhitelist
    public float getMaxHealth() {
        return target.getMaxHealth();
    }

    @LuaWhitelist
    public float getHealthPercentage(){
        return target.getHealth() / target.getMaxHealth();
    }

    @LuaWhitelist
    public float getArmor(){
        return target.getArmor();
    }

    @LuaWhitelist
    public int getDeathTime(){
        return target.deathTime;
    }

    @LuaWhitelist
    public List<String> getStatusEffects(){
        List<String> effects = new ArrayList<>();

        for (StatusEffectInstance effect : target.getStatusEffects()) {
            effects.add(Registry.STATUS_EFFECT.getId(effect.getEffectType()).toString());
        }

        return effects;
    }

    @LuaWhitelist
    public StatusEffectInstance getStatusEffect(String id){
        Identifier effectID = Identifier.tryParse(id);
        if(effectID == null)
            return null;
        StatusEffect effect = Registry.STATUS_EFFECT.get(effectID);

        return target.getStatusEffect(effect);
    }

    @LuaWhitelist
    public int getStuckArrowCount(){
        return target.getStuckArrowCount();
    }

    @LuaWhitelist
    public int getStingerCount(){
        return target.getStingerCount();
    }

    @LuaWhitelist
    public boolean isLeftHanded(){
        return target.getMainArm() == Arm.LEFT;
    }

    @LuaWhitelist
    public boolean isUsingItem(){
        return target.isUsingItem();
    }

    @LuaWhitelist
    public String getActiveHand(){
        return target.getActiveHand().toString().toLowerCase(Locale.ROOT);
    }

    @LuaWhitelist
    public ItemStack getActiveItem(){
        return target.getActiveItem();
    }

    @LuaWhitelist
    public ItemStack getHeldItem(String hand){
        ItemStack targetStack = null;

        if(hand.equals("right"))
            targetStack = target.getMainHandStack();
        if(hand.equals("left"))
            targetStack =  target.getOffHandStack();

        return (targetStack != null && targetStack.isEmpty()) ? null : targetStack;
    }

    @LuaWhitelist
    public ItemStack getHeldItem(int index){
        ItemStack targetStack = null;

        if(index == 1)
            targetStack = target.getMainHandStack();
        if(index == 2)
            targetStack =  target.getOffHandStack();

        return (targetStack != null && targetStack.isEmpty()) ? null : targetStack;
    }

    @LuaWhitelist
    public String getRecentDamageSource(){
        DamageSource ds = target.getRecentDamageSource();
        return ds == null ? null : ds.getName();
    }
}
