package net.blancworks.figura.avatar.script.api.pings;

import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;

public class PingWrapper extends ObjectWrapper<PingFunction> {

    public void __call(Object... args){
        target.run(args);
    }
}
