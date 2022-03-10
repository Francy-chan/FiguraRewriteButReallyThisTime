package net.blancworks.figura.serving.dealers.backend.connection.components;

import net.blancworks.figura.FiguraMod;
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

    public void sendPing(int pingID, byte[] pingBytes) {
        try (var ctx = getContext(MessageNames.SEND_PING)) {
            ctx.writer.writeInt(pingID);
            ctx.writer.writeInt(pingBytes.length);
            ctx.writer.write(pingBytes);
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }
    }

    public void onPingResponse(ByteBuffer buffer) {
        String response = ByteBufferExtensions.readResult(buffer);

        if (response != null)
            FiguraMod.LOGGER.error(response);
    }

    public void onPingReceived(ByteBuffer buffer) {
        UUID id = UUID.fromString(ByteBufferExtensions.readString(buffer));
        int pingID = buffer.getInt();
        int bytesCount = buffer.getInt();
        byte[] pingBytes = new byte[bytesCount];
        buffer.get(pingBytes);

        FiguraMod.LOGGER.info("Got ping of " + bytesCount + " bytes from user " + id);
    }

}
