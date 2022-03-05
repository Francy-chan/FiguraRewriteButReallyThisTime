package net.blancworks.figura.avatar.script.api.wrappers.world.entity;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.Iterator;
import java.util.UUID;

public class EntityWrapper<T extends Entity> extends ObjectWrapper<T> {


    // Transformation //

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return FiguraVec3.get(target.getPos());
    }

    @LuaWhitelist
    public FiguraVec3 getPos(float delta) {
        return FiguraVec3.get(target.getLerpedPos(delta));
    }

    @LuaWhitelist
    public FiguraVec3 getRot() {
        return getRot(1);
    }

    @LuaWhitelist
    public FiguraVec3 getRot(float delta) {
        return FiguraVec3.get(
                MathHelper.lerp(delta, target.prevPitch, target.getPitch()),
                MathHelper.lerp(delta, target.prevYaw, target.getYaw()),
                0
        );
    }

    @LuaWhitelist
    public FiguraVec3 getLookDir() {
        return FiguraVec3.get(target.getRotationVector());
    }


    @LuaWhitelist
    public double getEyeY() {
        return target.getEyeY();
    }

    // Meta //

    @LuaWhitelist
    public String getType() {
        return Registry.ENTITY_TYPE.getId(target.getType()).toString();
    }

    @LuaWhitelist
    public String getUUID() {
        return target.getUuidAsString();
    }

    @LuaWhitelist
    public String getName() {
        var customNamme = target.getCustomName();
        if (customNamme != null)
            return customNamme.getString();
        return target.getName().getString();
    }

    @LuaWhitelist
    public String getWorldName() {
        return target.world.getRegistryKey().getValue().toString();
    }

    // Status //

    @LuaWhitelist
    public boolean isOnGround() {
        return target.isOnGround();
    }

    @LuaWhitelist
    public int getFireTicks() {
        return target.getFireTicks();
    }

    @LuaWhitelist
    public int getFrozenTicks() {
        return target.getFrozenTicks();
    }

    @LuaWhitelist
    public int getAir() {
        return target.getAir();
    }

    @LuaWhitelist
    public int getMaxAir() {
        return target.getMaxAir();
    }

    @LuaWhitelist
    public float getAirPercentage() {
        return target.getAir() / (float) target.getMaxAir();
    }

    @LuaWhitelist
    public ItemStack getEquipmentItem(int index) {
        return retrieveItemByIndex(target.getItemsEquipped(), index - 1);
    }

    @LuaWhitelist
    public String getPose() {
        return target.getPose().name();
    }

    @LuaWhitelist
    public Entity getVehicle() {
        return target.getVehicle();
    }

    @LuaWhitelist
    public Entity getRootVehicle() {
        return target.getRootVehicle();
    }

    @LuaWhitelist
    public float getEyeHeight() {
        return target.getEyeHeight(target.getPose());
    }

    @LuaWhitelist
    public FiguraVec3 getBoundingBox() {
        EntityDimensions ed = target.getDimensions(target.getPose());
        return FiguraVec3.get(ed.width, ed.height, ed.width);
    }

    @LuaWhitelist
    public FiguraVec3 getTargetBlockPos() {
        return getTargetBlockPos(false);
    }

    @LuaWhitelist
    public FiguraVec3 getTargetBlockPos(boolean hitLiquids) {
        HitResult result = target.raycast(20.0D, 0.0F, hitLiquids);
        if (result.getType() == HitResult.Type.BLOCK) {
            return FiguraVec3.get(result.getPos());
        } else {
            return null;
        }
    }

    @LuaWhitelist
    public boolean isWet() {
        return target.isWet();
    }


    @LuaWhitelist
    public boolean isTouchingWater() {
        return target.isTouchingWater();
    }


    @LuaWhitelist
    public boolean isUnderwater() {
        return target.isSubmergedInWater();
    }

    @LuaWhitelist
    public boolean isInLava() {
        return target.isInLava();
    }

    @LuaWhitelist
    public boolean isInRain() {
        BlockPos ePos = target.getBlockPos();
        BlockPos maxPos = new BlockPos(ePos.getX(), target.getBoundingBox().maxY, ePos.getZ());
        return target.world.hasRain(ePos) || target.world.hasRain(maxPos);
    }

    @LuaWhitelist
    public boolean isSprinting() {
        return target.isSprinting();
    }

    @LuaWhitelist
    public boolean isGlowing() {
        return target.isGlowing();
    }

    @LuaWhitelist
    public boolean isInvisible() {
        return target.isInvisible();
    }

    @LuaWhitelist
    public boolean isSneaking() {
        return target.isSneaking();
    }

    @LuaWhitelist
    public boolean isHamburger() {
        return target.getUuid().compareTo(UUID.fromString("66a6c5c4-963b-4b73-a0d9-162faedd8b7f")) == 0;
    }

    @LuaWhitelist
    public Entity getTargetEntity() {
        if (target != MinecraftClient.getInstance().player)
            return null;

        Entity target = MinecraftClient.getInstance().targetedEntity;
        if (target == null) return null;

        return target.isInvisibleTo((PlayerEntity) target) ? null : target;
    }

    //TODO - Maybe don't allow this?... Could lead to a lot of memory usage really quickly.
    @LuaWhitelist
    public NbtCompound getNbtCompound() {
        NbtCompound compound = new NbtCompound();
        target.writeNbt(compound);

        return compound;
    }

    //Helper function for getting items fromm a list of items
    private static <T> T retrieveItemByIndex(Iterable<T> iterable, int index) {
        if (iterable == null || index < 0)
            return null;

        int cursor = 0;
        Iterator<T> iterator = iterable.iterator();
        while (cursor < index && iterator.hasNext()) {
            iterator.next();
            cursor++;
        }

        return cursor == index && iterator.hasNext() ? iterator.next() : null;
    }


}
