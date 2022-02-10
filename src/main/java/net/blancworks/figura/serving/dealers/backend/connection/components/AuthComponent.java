package net.blancworks.figura.serving.dealers.backend.connection.components;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.messages.MessageNames;
import net.blancworks.figura.utils.ByteBufferExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class AuthComponent extends ConnectionComponent {
    // -- Variables -- //
    public boolean isAuthenticated = false;
    private final byte[] authKey = new byte[20];
    private CompletableFuture authenticationFuture = null;

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    // -- Constructors -- //

    public AuthComponent(FiguraBackendDealer.FiguraWebSocketClient client) {
        super(client);

        registerReader(MessageNames.AUTH_REQUEST, this::onAuthRequested);
        registerReader(MessageNames.AUTH_COMPLETE, this::onAuthConfirmed);
    }

    // -- Functions -- //

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Ensures the client has been authenticated, or attempts to authenticate if they aren't.
     */
    public boolean ensureAuth() {

        if (isAuthenticated) return true;

        //Verify we're logged in to... Some account.
        {
            MinecraftClient client = MinecraftClient.getInstance();
            Session s = client.getSession();

            if (s.getAccessToken().equals("FabricMC"))
                return false;
        }

        //If authentication isn't started, or has completed exceptionally
        if (authenticationFuture == null || authenticationFuture.isCompletedExceptionally()) {
            //Attempt to authenticate with mojang
            authenticationFuture = CompletableFuture.runAsync(() -> {
                //Send mojang session server authentication data
                try {
                    MinecraftClient client = MinecraftClient.getInstance();
                    Session s = client.getSession();
                    String serverID = Hex.encodeHexString(authKey);

                    client.getSessionService().joinServer(s.getProfile(), s.getAccessToken(), serverID);

                    //Let the backend know we've authenticated with mojang.
                    sendAuthConfirmed();
                } catch (Exception e) {
                    //FiguraMod.LOGGER.error(e);
                }

                //Set auth future to null, we'll try again in 5 seconds.
                authenticationFuture = null;
            });
        }

        return false;
    }

    /**
     * Called when the backend requests we authenticate the connection.
     */
    public void onAuthRequested(ByteBuffer bytes) {
        bytes.get(authKey);
    }

    /**
     * Sent by the client to the backend, to let the server know we gave mojang the backend's key.
     */
    public synchronized void sendAuthConfirmed() {
        try (var ctx = getContext(MessageNames.AUTH_COMPLETE)) {
            ByteBufferExtensions.writeString(ctx.writer, MinecraftClient.getInstance().getSession().getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the backend confirms mojang has their key, and the connection is authenticated and ready.
     */
    public void onAuthConfirmed(ByteBuffer bytes) {
        isAuthenticated = true;
        FiguraMod.LOGGER.info("Backend connection authenticated! We can upload avatars now :D");

        socket.avatarServer.uploadAvatar(new byte[512]);
    }

}
