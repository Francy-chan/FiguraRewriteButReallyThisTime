package net.blancworks.figura.avatar.pings;

import java.util.LinkedList;
import java.util.Queue;

public class PingManager {
    public final Queue<Ping> outgoingPings = new LinkedList<>();
    public final Queue<Ping> incomingPings = new LinkedList<>();

    public void tick(){

    }
}
