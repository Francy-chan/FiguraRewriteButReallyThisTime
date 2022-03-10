package net.blancworks.figura.avatar.script.api.pings;

import net.blancworks.figura.avatar.pings.Ping;
import net.blancworks.figura.avatar.script.lua.types.LuaFunction;

import java.util.Queue;

public class PingFunction {

    public LuaFunction luaFunction;
    private final Queue<Ping> outputPings;
    public final int slotID;
    public short id;

    public PingFunction(int slot, short id, Queue<Ping> outputPings) {
        this.slotID = slot;
        this.id = id;
        this.outputPings = outputPings;
    }

    //Called when lua calls this ping.
    //Should send the appropriate args and data where required.
    void run(Object... args) {
        Ping newPing = new Ping();
        newPing.set(slotID, id, args);

        outputPings.add(newPing);
    }
}
