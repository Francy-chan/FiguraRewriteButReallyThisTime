package net.blancworks.figura.serving.dealers.backend.network;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import net.blancworks.figura.FiguraMod;

import java.util.concurrent.CompletableFuture;

public class FiguraNetworkManager {
    // -- Variables -- //
    private static final int TIMEOUT_TIME = 5 * 1000; // 5 * 1000 ms
    private static final int RECONNECT_COOLDOWN = 5 * 20; // 5 * 20 ticks
    private int timerValue = 0;


    private WebSocketFactory socketFactory = null;
    private WebSocket currentWebsocket = null;
    private boolean isConnecting = false;


    // -- Constructors -- //

    public FiguraNetworkManager() {
        socketFactory = createWebSocketFactory();
    }

    protected WebSocketFactory createWebSocketFactory() {
        WebSocketFactory factory = new WebSocketFactory();
        factory.setServerName("figuranew.blancworks.org");
        return factory;
    }

    // -- Functions -- //

    public void tick() {
        if (timerValue > 0) {
            timerValue--;
            return;
        }

        //if(!ensureConnection()){
            //timerValue = RECONNECT_COOLDOWN; // Wait for 5 seconds before trying again.
            //return;
        //}
    }

    public String targetAddress() {
        return "https://figura.blancworks.org";
    }

    public boolean ensureConnection() {

        //If the current websocket is null, create one.
        if (currentWebsocket == null) {
            try {
                currentWebsocket = socketFactory.createSocket(targetAddress(), TIMEOUT_TIME);
                currentWebsocket.setPingInterval(15 * 1000);
                isConnecting = true;

                //Connect asynchronously so we don't stuff up the main game thread.
                CompletableFuture.runAsync(() -> {
                    try{
                        currentWebsocket.connect();
                    } catch (Exception e){
                        FiguraMod.LOGGER.error(e);
                    }

                    //Set isConnecting to false, regardless of if we connected or not.
                    isConnecting = false;
                });
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }

            //We had to create a websocket, so connection isn't secure.
            return false;
        }

        if(!currentWebsocket.isOpen()) {
            //If connection is closed, but we're not connecting, then the connection was lost (or never made), so clear the value.
            if(!isConnecting) currentWebsocket = null;
            return false;
        }

        //Socket exists, and is open. We're good to go!
        return true;
    }

}
