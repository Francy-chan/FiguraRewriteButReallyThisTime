package net.blancworks.figura.avatar.script.api.general;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.client.MinecraftClient;

public class RendererAPI extends ObjectWrapper<RendererAPI> {

    @LuaWhitelist
    public boolean isFirstPerson() {
        if(!isHost) return false;
        return MinecraftClient.getInstance().options.getPerspective().isFirstPerson();
    }

    @LuaWhitelist
    public boolean isCameraBackwards() {
        if(!isHost) return false;
        return MinecraftClient.getInstance().options.getPerspective().isFrontView();
    }

    @LuaWhitelist
    public FiguraVec3 getCameraPos(){
        return FiguraVec3.get(MinecraftClient.getInstance().gameRenderer.getCamera().getPos());
    }

    @LuaWhitelist
    public FiguraVec3 getCameraRot(){
        return FiguraVec3.get(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation().toEulerXyz());
    }
}
