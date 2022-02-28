package net.blancworks.figura.serving.dealers.backend;

import com.google.common.io.LittleEndianDataOutputStream;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.dealers.backend.connection.components.AuthComponent;
import net.blancworks.figura.serving.dealers.backend.connection.components.AvatarServerComponent;
import net.blancworks.figura.serving.dealers.backend.connection.components.ConnectionComponent;
import net.blancworks.figura.serving.dealers.backend.connection.components.SubscriptionComponent;
import net.blancworks.figura.serving.dealers.backend.messages.MessageNames;
import net.blancworks.figura.serving.dealers.backend.messages.MessageRegistry;
import net.blancworks.figura.serving.dealers.backend.messages.MessageSenderContext;
import net.blancworks.figura.serving.dealers.backend.requests.EntityAvatarRequest;
import net.blancworks.figura.serving.dealers.backend.requests.RunnableDealerRequest;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.blancworks.figura.ui.FiguraToast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Identifier;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.ConnectException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public class FiguraBackendDealer extends FiguraDealer {
    // -- Variables -- //
    public static final Identifier ID = new Identifier("figura", "backend");

    // Cooldown //
    private static final int TIMEOUT_TIME = 5 * 1000; // 5 * 1000 ms
    private static final int RECONNECT_COOLDOWN = 5 * 20; // 5 * 20 ticks
    private int timerValue = 0;

    // Connection //
    private FiguraWebSocketClient websocket;
    private boolean isConnecting = false;
    private boolean isEquipping = false;
    private boolean isDeleting = false;

    private final HashSet<UUID> subscribedUUIDs = new HashSet<>();

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
    public AvatarHolder getHolder(Entity entity) {
        if (entity instanceof PlayerEntity pe) {
            UUID id = pe.getGameProfile().getId();

            //Offline-mode catch.
            if (id != null) {
                return getHolder(id);
            }
        }

        return null;
    }

    @Override
    public synchronized AvatarHolder getHolder(UUID id) {
        //TODO - Sanitize against player UUIDs...

        //Get a holder from the group with this UUID.
        AvatarHolder holder = getOrCreateGroup(id).getHolder();

        // If the entity is the local player, don't subscribe to ourselves (backend does this automatically)
        // Otherwise, subscribe to this UUID.
        UUID profileID = MinecraftClient.getInstance().getSession().getProfile().getId();
        if (!id.equals(profileID))
            subscribedUUIDs.add(id);

        return holder;
    }


    @Override
    protected AvatarGroup constructNewGroup(UUID id) {
        ensureConnection();

        var ng = super.constructNewGroup(id);

        //Add request for list of avatars.
        requestQueue.add(new EntityAvatarRequest(ng.getHolder(), id, this));

        return ng;
    }

    //Called when a group of a given UUID is cleaned up.
    @Override
    protected synchronized void removeGroup(UUID id) {
        super.removeGroup(id);

        subscribedUUIDs.remove(id);
    }

    // Connection //


    protected FiguraWebSocketClient getClient() {
        try {
            FiguraWebSocketClient client = new FiguraWebSocketClient(this, new URI("wss://figura-backend-v1.blancworks.org/connect/"));
            client.setConnectionLostTimeout(15);

            return client;
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }

        return null;
    }

    public synchronized boolean ensureConnection() {

        //If we're already connecting, we're not connected, so....
        if (isConnecting) {
            return false;
        }

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

    public FiguraWebSocketClient getSocket() {
        ensureConnection();
        return websocket;
    }


    // -- Functions -- //

    private boolean checkConnection() {
        if (websocket == null) {
            ensureConnection();
            FiguraToast.sendToast("figura.backend.error", "figura.backend.connect.not");
            return false;
        }

        if (!websocket.auth.isAuthenticated) {
            FiguraToast.sendToast("figura.backend.error", "figura.backend.auth.error.noauth");
            return false;
        }

        return true;
    }

    public void equipAvatar(NbtCompound uploadData, Consumer<byte[]> onComplete) {
        if (!checkConnection())
            return;

        isEquipping = true;
        requestQueue.add(new RunnableDealerRequest(() -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream nbtDataStream = new DataOutputStream(baos);

                NbtIo.writeCompressed(uploadData, nbtDataStream);

                byte[] result = baos.toByteArray();

                websocket.avatarServer.equipAvatar(result, (a) -> {
                    if (a == null) {
                        onComplete.accept(result);
                        FiguraToast.sendToast("figura.backend.equip.success");
                    } else {
                        FiguraToast.sendToast("figura.backend.equip.failed", "figura.backend." + a);
                    }
                });
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }
            isEquipping = false;
        }));
    }

    public void deleteAvatar(Consumer<String> onComplete) {
        if (!checkConnection())
            return;

        isDeleting = true;
        requestQueue.add(new RunnableDealerRequest(() -> {
            try {
                websocket.avatarServer.deleteAvatar(response -> {
                    if (response == null) {
                        onComplete.accept("delete.success");
                        FiguraToast.sendToast("figura.backend.delete.success");
                    } else {
                        onComplete.accept("delete.failed");
                        FiguraToast.sendToast("figura.backend.delete.fail", "figura.backend." + response);
                    }
                });
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }
            isDeleting = false;
        }));
    }

    // -- Nested Types -- //

    public interface MessageReaderFunction {
        void run(ByteBuffer dis);
    }

    //Used to clean up subscriptions for users who are unloaded.
    private class AvatarGroupCleanTask implements Runnable {
        private final UUID targetID;

        public AvatarGroupCleanTask(UUID id) {
            targetID = id;
        }

        //Called to clean up the target
        @Override
        public void run() {
            subscribedUUIDs.remove(targetID);
        }
    }

    /**
     * Handles the websocket connection to the backend!
     */
    public static class FiguraWebSocketClient extends WebSocketClient {
        // -- Variables -- //
        public final FiguraBackendDealer backend;

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
        public final SubscriptionComponent subscriptionComponent;

        // -- Constructors -- //
        public FiguraWebSocketClient(FiguraBackendDealer backend, URI serverUri) {
            super(serverUri);
            this.backend = backend;
            senderContext = new MessageSenderContext(this);

            //The getMessageRegistry message is always message 0.
            readers.put(MessageNames.MESSAGE_REGISTRY_INIT, MessageRegistry::readRegistry);

            // Components //
            auth = addComponent(new AuthComponent(this));
            avatarServer = addComponent(new AvatarServerComponent(this));
            subscriptionComponent = addComponent(new SubscriptionComponent(this, backend.subscribedUUIDs));
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
            backend.isConnecting = false;

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
            try {
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
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            FiguraMod.LOGGER.error("Disconnected from backend with reason " + code + ":" + reason + " remote : " + remote);

            backend.isConnecting = false;
        }

        @Override
        public void onError(Exception ex) {
            FiguraMod.LOGGER.error(ex);

            if (ex instanceof ConnectException)
                backend.isConnecting = false;
        }

        @Override
        public void onWebsocketPong(WebSocket conn, Framedata f) {
            super.onWebsocketPong(conn, f);

            //FiguraMod.LOGGER.info("Heartbeat returned from server");
        }
    }
}
