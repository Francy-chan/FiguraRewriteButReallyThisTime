package net.blancworks.figura.serving;

import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.FiguraDevelopmentBackendDealer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages all tasks related to dealers, serving avatars, that sort of thing.
 */
public class FiguraHouse {

    // -- Variables -- //
    public static final List<FiguraDealer> registeredDealers = new ArrayList<>();
    private static int countdown = 20;

    private static final FiguraBackendDealer backend = new FiguraBackendDealer();
    private static final FiguraDevelopmentBackendDealer devBackend = new FiguraDevelopmentBackendDealer();

    private static final boolean useDeveloperBackend = true;


    // -- Functions -- //

    public static void init() {
        //Register a function that will clear all events from dealers when you leave a server.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            for (FiguraDealer dealer : registeredDealers)
                dealer.clearRequests();
        });

        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (countdown > 0) {
                countdown--;
                return;
            }

            //if(client.world != null)
            tick();
        });


        registerDefaultDealers();
    }

    private static void registerDefaultDealers() {
        registerDealer(new FiguraLocalDealer());
        registerDealer(getBackend());
    }

    public static void registerDealer(FiguraDealer dealer) {
        registeredDealers.add(dealer);
    }


    public static void tick() {
        for (FiguraDealer dealer : registeredDealers)
            dealer.tick();
    }

    public static FiguraEntityMetadata getEntityMetadata(Entity targetEntity) {
        FiguraEntityMetadata newMetadata = new FiguraEntityMetadata(targetEntity);

        //Put avatar groups in from the dealers
        for (FiguraDealer dealer : registeredDealers)
            newMetadata.addGroup(dealer.getID(), dealer.getGroup(targetEntity));

        return newMetadata;
    }

    public static FiguraBackendDealer getBackend() {
        return useDeveloperBackend ? devBackend : backend;
    }
}
