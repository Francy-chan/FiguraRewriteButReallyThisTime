package net.blancworks.figura.serving.dealers.backend;

import net.blancworks.figura.FiguraMod;

import java.net.URI;

public class FiguraDevelopmentBackendDealer extends FiguraBackendDealer{
    @Override
    protected FiguraBackendDealer.FiguraWebSocketClient getClient() {
        try {
            return new FiguraWebSocketClient(new URI("ws://localhost:6050/connect"));
        } catch (Exception e){
            FiguraMod.LOGGER.error(e);
        }

        return null;
    }
}
