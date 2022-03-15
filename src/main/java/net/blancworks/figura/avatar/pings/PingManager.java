package net.blancworks.figura.avatar.pings;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;

import java.util.LinkedList;
import java.util.Queue;

public class PingManager {
    private final FiguraAvatar figuraAvatar;

    public final Queue<Ping> outgoingPings = new LinkedList<>();
    public final Queue<Ping> incomingPings = new LinkedList<>();

    public PingManager(FiguraAvatar figuraAvatar) {
        this.figuraAvatar = figuraAvatar;
    }

    public void tick() {
        //  Process outgoing pings
        while (outgoingPings.size() > 0) {
            Ping p = outgoingPings.poll();

            if(figuraAvatar.getScript().isHost)
                FiguraBackendDealer.queueNetworkPing(p);

            incomingPings.add(p);
        }

        // Process incoming pings
        while(incomingPings.size() > 0){
            Ping p = incomingPings.poll();

            figuraAvatar.getScript().luaState.pingsAPI.handlePing(p);
        }
    }
}
