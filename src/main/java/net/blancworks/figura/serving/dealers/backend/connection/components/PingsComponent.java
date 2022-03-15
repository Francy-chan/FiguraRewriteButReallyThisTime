package net.blancworks.figura.serving.dealers.backend.connection.components;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.pings.Ping;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.messages.MessageNames;
import net.blancworks.figura.utils.ByteBufferExtensions;

import java.nio.ByteBuffer;
import java.util.UUID;

public class PingsComponent extends ConnectionComponent {
    // -- Variables -- //


    // -- Constructors -- //

    public PingsComponent(FiguraBackendDealer.FiguraWebSocketClient dealer) {
        super(dealer);

        registerReader(MessageNames.SEND_PING, this::onPingResponse);
        registerReader(MessageNames.RECEIVE_PING, this::onPingReceived);
    }


    // -- Functions -- //


    @Override
    public void tick() {
        super.tick();

        while (true) {
            var p = FiguraBackendDealer.dequeueNetworkPing();
            if (p == null) break;

            //Do nothing when unauthenticated.
            if (!socket.auth.isAuthenticated) continue;

            sendPing(p);
        }
    }

    public void sendPing(Ping p) {
        try (var ctx = getContext(MessageNames.SEND_PING)) {
            p.write(ctx);
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }
    }

    public void onPingResponse(ByteBuffer buffer) {
        String response = ByteBufferExtensions.readResult(buffer);

        if (response != null) FiguraMod.LOGGER.error(response);
    }

    public void onPingReceived(ByteBuffer buffer) {
        try {
            Ping p = new Ping();
            p.read(buffer);
        } catch (Exception e) {
            // Ignore bad pings
        }
    }

}
