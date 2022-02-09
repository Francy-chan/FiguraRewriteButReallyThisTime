package net.blancworks.figura.serving.dealers.backend;

import com.google.common.io.LittleEndianDataOutputStream;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.entity.AvatarGroup;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.methods.HttpPost;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FiguraBackendDealer extends FiguraDealer {
    // -- Variables -- //
    private static final Identifier ID = new Identifier("figura", "backend");
    private static final int TIMEOUT_TIME = 5 * 1000; // 5 * 1000 ms
    private static final int RECONNECT_COOLDOWN = 5 * 20; // 5 * 20 ticks

    private int timerValue = 0;

    private FiguraWebSocketClient currentWebsocket = null;
    private boolean isConnecting = false;

    // -- Constructors -- //

    public FiguraBackendDealer() {
    }


    // -- Functions -- //

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public void tick() {
        if (timerValue > 0) {
            timerValue--;
            return;
        }

        if (!ensureConnection()) {
            timerValue = RECONNECT_COOLDOWN; // Wait for 5 seconds before trying again.
            return;
        }

        super.tick();
    }

    @Override
    protected <T extends Entity> void requestForEntity(AvatarGroup group, T entity) {
        DealerRequest entityRequest = new DealerRequest(() -> {

        });

        requestQueue.add(entityRequest);
    }

    protected String targetAddress() {
        return "https://figura.blancworks.org";
    }

    protected FiguraWebSocketClient getClient() throws URISyntaxException {
        FiguraWebSocketClient client = new FiguraWebSocketClient(new URI(targetAddress()));

        //Manually trust figura's certificate (lazy dev zandra doesn't wanna replace a certificate every few months, boo hoo)
        try {
            //Init keystore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            Path ksPath = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
            keyStore.load(Files.newInputStream(ksPath), "changeit".toCharArray());

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certStream = FiguraMod.class.getResourceAsStream("FiguraNewCertificate.cer");

            try {
                Certificate crt = cf.generateCertificate(certStream);
                keyStore.setCertificateEntry("DSTRootCAX3", crt);
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }

            certStream.close();

            //Create SSL context and socket factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            SSLSocketFactory factory = sslContext.getSocketFactory();

            //Set client to use the socket factory we created
            client.setSocketFactory(factory);
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }

        return client;
    }

    public boolean ensureConnection() {

        //If the current websocket is null, create one.
        if (currentWebsocket == null) {
            try {
                isConnecting = true;

                //Create & start connecting
                currentWebsocket = getClient();
                currentWebsocket.connect();
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }

            //We had to create a websocket, so connection isn't ready.
            return false;
        }

        //If websocket exists, but isn't open....
        if (!currentWebsocket.isOpen()) {
            //If connection is closed, but we're not connecting, then the connection was lost (or never made).
            if (!isConnecting) {
                //Clear websocket so we can re-create it later.
                currentWebsocket = null;

                //Reset active requests
                for (int i = 0; i < activeRequests.length; i++) {
                    if (activeRequests[i] == null) continue;

                    activeRequests[i].isInProgress = false;
                    activeRequests[i] = null;
                }
            }

            //Websocket isn't open, so, connection isn't ready.
            return false;
        }

        //Authentication isn't done,
        if (!currentWebsocket.ensureAuth())
            return false;

        //Socket exists, and is open. We're good to go!
        return true;
    }

    // -- Nested Types -- //

    /**
     * Handles the websocket connection to the backend!
     */
    protected class FiguraWebSocketClient extends WebSocketClient {
        private interface MessageReaderFunction {
            void run(ByteBuffer dis);
        }

        // -- Variables -- //
        private static final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        private static final LittleEndianDataOutputStream outputStream = new LittleEndianDataOutputStream(bos);

        //Message registry
        private final HashMap<String, MessageReaderFunction> readersByID = new HashMap<>();
        private final HashMap<String, Integer> IDToIndex = new HashMap<>();
        private final List<MessageReaderFunction> readersByIndex = new ArrayList<>();

        private boolean hasRegistry = false;

        // -- Constructors -- //

        public FiguraWebSocketClient(URI serverUri) {
            super(serverUri);

            //The getMessageRegistry message is always message 0.
            readersByIndex.add(this::onGetMessageRegistry);

            // Register messages by ID. //

            //Message registry
            readersByID.put("msg_reg", this::onGetMessageRegistry);

            //Auth
            readersByID.put("auth_req", this::onAuthRequested);
            readersByID.put("auth_ok", this::onAuthConfirmed);
        }

        // -- Functions -- //

        // Events //

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            //Connected!
            isConnecting = false;

            FiguraMod.LOGGER.info("Connection with backend established!");
        }

        @Override
        public void onMessage(String message) {

        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            //Change to little endian. (C# uses little)
            bytes.order(ByteOrder.LITTLE_ENDIAN);

            //Create DataInputStream from message, for easier reading.
            int messageIndex = bytes.getInt();

            //FiguraMod.LOGGER.info("Got message with numerical ID " + messageIndex);

            //If there's no registry, and this message isn't a registry message, do nothing.
            if (!hasRegistry && messageIndex != 0)
                return;

            //Ignore out-of-bounds messages.
            if (messageIndex < 0 || messageIndex >= readersByIndex.size())
                return;

            //Get the message matching what the server is sending.
            MessageReaderFunction function = readersByIndex.get(messageIndex);

            //Run message reader.
            if (function != null) function.run(bytes);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {

        }

        @Override
        public void onError(Exception ex) {
            FiguraMod.LOGGER.error(ex);

            if (ex instanceof ConnectException)
                isConnecting = false;
        }

        // Messages //

        private boolean setMessage(String id) throws IOException {
            Integer index = IDToIndex.get(id);
            if(index == null) return false;

            outputStream.writeInt(index);
            return true;
        }

        private void sendCurrentMessage(){
            byte[] data = bos.toByteArray();
            bos.reset();
            send(data);
        }

        //Reads an ID from a data input stream, by decoding a UTF-8 string.
        private String readString(ByteBuffer bytes) {
            //We use a roundabout method because Java encodes/decodes strings funny sometimes.
            int byteCount = bytes.getInt();
            byte[] idBytes = new byte[byteCount];
            bytes.get(idBytes);
            return new String(idBytes, StandardCharsets.UTF_8);
        }

        private void writeString(String s) throws IOException {
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            outputStream.writeInt(bytes.length);
            outputStream.write(bytes);
        }

        /**
         * Handles the message registry being received from the backend.
         */
        public void onGetMessageRegistry(ByteBuffer bytes) {
            //How many messages we expect to have.
            //Limit to 512 for safety.
            int idCount = bytes.getInt();
            idCount = MathHelper.clamp(idCount, 0, 512);

            //NOTE - IGNORE FIRST ID!!! It's always the Message Registry.
            for (int i = 1; i < idCount; i++) {
                String id = readString(bytes);
                //Get message by ID, put it down where the backend says it should be.
                readersByIndex.add(readersByID.get(id));
                //Also record the ID to index, for sent messages.
                IDToIndex.put(id, i);
            }

            //Registry has been read!
            hasRegistry = true;
        }



        // Auth //
        public boolean isAuthenticated = false;
        private final byte[] authKey = new byte[20];
        private CompletableFuture authenticationFuture = null;

        private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
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
        private boolean ensureAuth() {

            if(isAuthenticated) return true;

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
        public synchronized void sendAuthConfirmed() throws IOException{
            if(setMessage(MessageNames.AUTH_COMPLETE)) {
                writeString(MinecraftClient.getInstance().getSession().getUsername());
                sendCurrentMessage();
            }
        }

        /**
         * Called when the backend confirms mojang has their key, and the connection is authenticated and ready.
         */
        public void onAuthConfirmed(ByteBuffer bytes) {
            isAuthenticated = true;
            FiguraMod.LOGGER.info("Backend connection authenticated! We can upload avatars now :D");
        }

    }
}
