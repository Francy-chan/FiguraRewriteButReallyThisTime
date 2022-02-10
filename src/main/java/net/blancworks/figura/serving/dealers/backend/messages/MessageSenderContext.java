package net.blancworks.figura.serving.dealers.backend.messages;

import com.google.common.io.LittleEndianDataOutputStream;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;

import java.io.ByteArrayOutputStream;

public class MessageSenderContext implements AutoCloseable {
    // -- Variables -- //

    public final FiguraBackendDealer.FiguraWebSocketClient client;
    private boolean isValid = false;

    private final ByteArrayOutputStream buffer;
    public final LittleEndianDataOutputStream writer;

    // -- Constructors -- //

    public MessageSenderContext(FiguraBackendDealer.FiguraWebSocketClient client) {
        this.client = client;
        buffer = FiguraBackendDealer.FiguraWebSocketClient.bos;
        writer = FiguraBackendDealer.FiguraWebSocketClient.outputStream;
    }


    // -- Functions -- //


    public void setMessageName(String messageName) {
        Integer id = MessageRegistry.tryGetID(messageName);
        if (id == null) {
            isValid = false;
            return;
        }

        try {
            writer.writeInt(id);
            isValid = true;
        }catch ( Exception e){
            FiguraMod.LOGGER.error(e);
            isValid = false;
        }
    }

    @Override
    public void close() {
        if (isValid)
            client.sendCurrentMessage();
        buffer.reset();
    }
}
