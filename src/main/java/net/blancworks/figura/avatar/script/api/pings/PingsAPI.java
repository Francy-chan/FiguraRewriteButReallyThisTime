package net.blancworks.figura.avatar.script.api.pings;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.avatar.script.lua.types.LuaFunction;

import java.util.HashMap;

public class PingsAPI extends ObjectWrapper<PingsAPI> {

    //Stores all pings by the name ID.
    private final HashMap<String, PingFunction> registeredPings = new HashMap<>();

    //The slot the avatar this API belongs to is located in.
    private final int slotId;
    private final FiguraAvatar avatar;

    //The last ID that was registered for pings.
    private short lastID = Short.MIN_VALUE;

    public PingsAPI(FiguraAvatar avatar) {
        this.slotId = avatar.slot;
        this.avatar = avatar;
    }

    @Override
    public Object getFallback(String key) {
        return registeredPings.get(key);
    }

    @Override
    public void setFallback(String key, Object value) {
        super.setFallback(key, value);

        if (value instanceof LuaFunction lf) {
            //Construct or get reference to ping at the existing ID.
            PingFunction targetPing = registeredPings.computeIfAbsent(key, (s) -> new PingFunction(slotId, lastID++, avatar.pingManager.outgoingPings));

            //Set the target lua function to the one provided.
            targetPing.luaFunction = lf;
        }

        throw new IllegalArgumentException("You can only assign functions to the pings table");
    }
}
