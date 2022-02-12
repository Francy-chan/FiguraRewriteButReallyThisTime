package net.blancworks.figura.serving.dealers.backend.connection.components;

import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.messages.MessageSenderContext;

public class ConnectionComponent {
    // -- Variables -- //
    protected final FiguraBackendDealer.FiguraWebSocketClient socket;

    // -- Constructors -- //

    public ConnectionComponent(FiguraBackendDealer.FiguraWebSocketClient dealer) {
        this.socket = dealer;
    }


    // -- Functions -- //

    public void tick(){}

    public MessageSenderContext getContext(String messageName){
        return socket.getContext(messageName);
    }

    public void registerReader(String id, FiguraBackendDealer.MessageReaderFunction reader){
        socket.registerReader(id, reader);
    }

}
