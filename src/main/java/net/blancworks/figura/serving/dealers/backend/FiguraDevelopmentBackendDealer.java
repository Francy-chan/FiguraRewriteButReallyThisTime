package net.blancworks.figura.serving.dealers.backend;

import java.net.URI;
import java.net.URISyntaxException;

public class FiguraDevelopmentBackendDealer extends FiguraBackendDealer{
    @Override
    protected FiguraBackendDealer.FiguraWebSocketClient getClient() throws URISyntaxException {
        return new FiguraWebSocketClient(new URI("ws://localhost:6050/connect"));
    }
}
