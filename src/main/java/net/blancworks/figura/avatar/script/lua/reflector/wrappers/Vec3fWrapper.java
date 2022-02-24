package net.blancworks.figura.avatar.script.lua.reflector.wrappers;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.minecraft.util.math.Vec3f;

@Deprecated
public class Vec3fWrapper extends ObjectWrapper<Vec3f> {

    // -- Functions -- //
    @LuaWhitelist
    public float getX(){
        return target.getX();
    }
    @LuaWhitelist
    public float getY(){
        return target.getY();
    }
    @LuaWhitelist
    public float getZ(){
        return target.getZ();
    }

    @LuaWhitelist
    public void set(float x, float y, float z){
        target.set(x,y,z);
    }

    @LuaWhitelist
    public void set(Vec3f other){
        target.set(other);
    }
}
