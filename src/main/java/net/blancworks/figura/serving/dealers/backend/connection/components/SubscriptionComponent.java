package net.blancworks.figura.serving.dealers.backend.connection.components;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.messages.MessageNames;
import net.blancworks.figura.utils.ByteBufferExtensions;

import java.util.*;

public class SubscriptionComponent extends ConnectionComponent {
    // -- Variables -- //
    private final HashSet<UUID> currentSubscriptions = new HashSet<>();
    private final HashSet<UUID> targetList;

    // -- Constructors -- //

    public SubscriptionComponent(FiguraBackendDealer.FiguraWebSocketClient dealer, HashSet<UUID> targetList) {
        super(dealer);
        this.targetList = targetList;
    }


    // -- Functions -- //


    @Override
    public void tick() {
        super.tick();

        //Do nothing when not authenticated.
        if (!socket.auth.isAuthenticated || targetList == null)
            return;

        List<UUID> newSubscriptions = new ArrayList<>();
        for (UUID uuid : targetList)
            if (currentSubscriptions.add(uuid))
                newSubscriptions.add(uuid);

        List<UUID> removedSubscriptions = new ArrayList<>();
        for (UUID uuid : currentSubscriptions)
            if(!targetList.contains(uuid))
                removedSubscriptions.add(uuid);
        removedSubscriptions.forEach(currentSubscriptions::remove);


        sendSubUpdateMessage(newSubscriptions, removedSubscriptions);
    }

    private void sendSubUpdateMessage(List<UUID> newSubscriptions, List<UUID> removedSubscriptions) {

        if(newSubscriptions.size() == 0 && removedSubscriptions.size() == 0)
            return;

        try (var ctx = getContext(MessageNames.SUBSCRIBE)) {

            //Write new
            ctx.writer.writeInt(newSubscriptions.size());
            for (UUID uuid : newSubscriptions)
                ByteBufferExtensions.writeString(ctx.writer, uuid.toString());

            //Write removed.
            ctx.writer.writeInt(removedSubscriptions.size());
            for (UUID uuid : removedSubscriptions)
                ByteBufferExtensions.writeString(ctx.writer, uuid.toString());

        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }
    }


}
