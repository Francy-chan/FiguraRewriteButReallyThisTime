package net.blancworks.figura.avatar.script.api.general;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.trust.TrustContainer;

public class MetaAPI extends ObjectWrapper<MetaAPI> {

    private final FiguraAvatar refAvatar;

    public MetaAPI(FiguraAvatar avatar){
        this.refAvatar = avatar;
    }

    @LuaWhitelist
    public int getInitLimit(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.INIT_INST);
    }

    @LuaWhitelist
    public int getTickLimit(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.TICK_INST);
    }

    @LuaWhitelist
    public int getRenderLimit(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.RENDER_INST);
    }

    @LuaWhitelist
    public int getMemoryLimit(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.MAX_MEM);
    }

    @LuaWhitelist
    public int getComplexityLimit(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.COMPLEXITY);
    }

    @LuaWhitelist
    public int getParticleLimit(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.PARTICLES);
    }

    @LuaWhitelist
    public int getSoundLimit(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.SOUNDS);
    }

    @LuaWhitelist
    public boolean getVanillaModification(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.VANILLA_MODEL_EDIT) == 1;
    }

    @LuaWhitelist
    public boolean getCustomSounds(){
        return refAvatar.trustContainer.get(TrustContainer.Trust.CUSTOM_SOUNDS) == 1;
    }
}
