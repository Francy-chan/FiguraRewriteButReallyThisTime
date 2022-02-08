package net.blancworks.figura.serving;

import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.dealers.FiguraLocalDealer;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
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


    // -- Functions -- //

    public static void init() {
        //Register a function that will clear all events from dealers when you leave a server.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            for (FiguraDealer dealer : registeredDealers)
                dealer.clearRequests();
        });


        registerDefaultDealers();
    }

    private static void registerDefaultDealers() {
        FiguraHouse.registerDealer(new FiguraLocalDealer());
    }

    public static void registerDealer(FiguraDealer dealer) {
        registeredDealers.add(dealer);
    }

    public static FiguraEntityMetadata getEntityMetadata(Entity targetEntity) {
        FiguraEntityMetadata newMetadata = new FiguraEntityMetadata(targetEntity);

        //Put avatar groups in from the dealers
        for (FiguraDealer dealer : registeredDealers)
            newMetadata.addGroup(dealer.getID(), dealer.getGroup(targetEntity));

        return newMetadata;
    }
}
