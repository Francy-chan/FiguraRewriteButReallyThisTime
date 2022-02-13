package net.blancworks.figura.serving.dealers.backend;

import com.google.common.io.LittleEndianDataOutputStream;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.dealers.backend.connection.components.AuthComponent;
import net.blancworks.figura.serving.dealers.backend.connection.components.AvatarServerComponent;
import net.blancworks.figura.serving.dealers.backend.connection.components.ConnectionComponent;
import net.blancworks.figura.serving.dealers.backend.messages.MessageNames;
import net.blancworks.figura.serving.dealers.backend.messages.MessageRegistry;
import net.blancworks.figura.serving.dealers.backend.messages.MessageSenderContext;
import net.blancworks.figura.serving.dealers.backend.requests.EntityAvatarRequest;
import net.blancworks.figura.serving.dealers.backend.requests.RunnableDealerRequest;
import net.blancworks.figura.serving.entity.AvatarGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FiguraBackendDealer extends FiguraDealer {
    // -- Variables -- //
    private static final Identifier ID = new Identifier("figura", "backend");

    // Cooldown //
    private static final int TIMEOUT_TIME = 5 * 1000; // 5 * 1000 ms
    private static final int RECONNECT_COOLDOWN = 5 * 20; // 5 * 20 ticks
    private int timerValue = 0;

    // Connection //
    private FiguraWebSocketClient websocket;
    private boolean isConnecting = false;
    private boolean isUploading = false;

    // -- Constructors -- //

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
        websocket.tick();
    }

    @Override
    protected <T extends Entity> void requestForEntity(AvatarGroup group, T entity) {
        if (entity instanceof PlayerEntity pe) {
            UUID id = pe.getGameProfile().getId();

            //Offline-mode catch.
            if(id != null)
                requestQueue.add(new EntityAvatarRequest(group, pe.getGameProfile().getId(), websocket));
        }
    }


    // Connection //


    protected FiguraWebSocketClient getClient() {
        try {
            FiguraWebSocketClient client = new FiguraWebSocketClient(new URI("https://figura.blancworks.org"));

            //Manually trust figura's certificate (lazy dev zandra doesn't wanna replace a certificate every few months, boo hoo)
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

            return client;
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }

        return null;
    }

    public boolean ensureConnection() {

        //If we're already connecting, we're not connected, so....
        if (isConnecting) return false;


        //If the websocket is null, we haven't tried connecting yet, so try.
        if (websocket == null) {
            websocket = getClient();

            isConnecting = true;
            websocket.connect();
            return false;
        }

        //If websocket is not open, but we're not actively connecting...
        if (!websocket.isOpen()) {
            //Clear websocket.
            websocket = null;

            //Reset active requests
            for (int i = 0; i < activeRequests.length; i++) {
                if (activeRequests[i] == null) continue;

                activeRequests[i].isInProgress = false;
                activeRequests[i] = null;
            }

            //Return false, as we had to start connecting.
            return false;
        }

        //Authentication isn't done,
        if (!websocket.auth.ensureAuth())
            return false;

        //Socket exists, and is open. We're good to go!
        return true;
    }


    // -- Functions -- //

    public void uploadAvatar(NbtCompound uploadData) {
        isUploading = true;
        this.requestQueue.add(new RunnableDealerRequest(() -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream nbtDataStream = new DataOutputStream(baos);

                NbtIo.writeCompressed(uploadData, nbtDataStream);

                byte[] result = baos.toByteArray();

                //websocket.avatarServer.uploadAvatar(result);
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }

            isUploading = false;
        }));
    }


    // -- Nested Types -- //

    public interface MessageReaderFunction {
        void run(ByteBuffer dis);
    }

    /**
     * Handles the websocket connection to the backend!
     */
    public class FiguraWebSocketClient extends WebSocketClient {
        // -- Variables -- //
        // Reader //
        private final HashMap<String, MessageReaderFunction> readers = new HashMap<>();

        // Writer //
        public static final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        public static final LittleEndianDataOutputStream outputStream = new LittleEndianDataOutputStream(bos);
        private final MessageSenderContext senderContext;

        // Components //
        public final ArrayList<ConnectionComponent> components = new ArrayList<>();
        public final AuthComponent auth;
        public final AvatarServerComponent avatarServer;

        // -- Constructors -- //
        public FiguraWebSocketClient(URI serverUri) {
            super(serverUri);
            senderContext = new MessageSenderContext(this);

            //The getMessageRegistry message is always message 0.
            readers.put(MessageNames.MESSAGE_REGISTRY_INIT, MessageRegistry::readRegistry);

            // Components //
            auth = addComponent(new AuthComponent(this));
            avatarServer = addComponent(new AvatarServerComponent(this));
        }

        private <T extends ConnectionComponent> T addComponent(T component) {
            components.add(component);
            return component;
        }

        // -- Functions -- //

        public void tick() {
            if (!isOpen()) return;

            for (ConnectionComponent component : components) component.tick();
        }

        public void registerReader(String id, MessageReaderFunction reader) {
            readers.put(id, reader);
        }

        // Messages //

        /**
         * Gets a message sender context to be used to build messages
         */
        public MessageSenderContext getContext(String messageName) {
            FiguraMod.LOGGER.info("Constructing message of name " + messageName);

            senderContext.setMessageName(messageName);
            return senderContext;
        }

        /**
         * Sends whatever message is currently constructed, and resets the construction of messages.
         */
        public void sendCurrentMessage() {
            byte[] data = bos.toByteArray();

            FiguraMod.LOGGER.info("Sending message of " + data.length + " bytes");
            send(data);
        }

        // Events //

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            //Connected!
            isConnecting = false;

            FiguraMod.LOGGER.info("Connection with backend established!");

            //Clear previous message registry when connection is opened.
            MessageRegistry.clear();
        }

        @Override
        public void onMessage(String message) {
            //Ignore string messages
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            //Change to little endian. (C# uses little)
            bytes.order(ByteOrder.LITTLE_ENDIAN);

            //Read message name from registry.
            String messageName = MessageRegistry.tryGetName(bytes.getInt());

            FiguraMod.LOGGER.info("Got message of name " + messageName);
            if (messageName == null) return;

            //Get the message matching what the server is sending.
            MessageReaderFunction function = readers.get(messageName);

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
    }
}
