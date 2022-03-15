package net.blancworks.figura.avatar.script.api.pings;

import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;

public class PingFunctionWrapper extends ObjectWrapper<PingFunction> {

    public static void __call(PingFunction target, Object... args){
        target.run(args);
    }
}
