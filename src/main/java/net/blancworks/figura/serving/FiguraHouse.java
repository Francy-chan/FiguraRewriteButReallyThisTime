package net.blancworks.figura.serving;

import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.dealers.backend.FiguraBackendDealer;
import net.blancworks.figura.serving.dealers.backend.FiguraDevelopmentBackendDealer;
import net.blancworks.figura.serving.dealers.local.FiguraLocalDealer;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.trust.TrustManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class manages all tasks related to dealers, serving avatars, that sort of thing.
 */
public class FiguraHouse {

    // -- Variables -- //
    public static final List<FiguraDealer> registeredDealers = new ArrayList<>();
    private static int countdown = 20;

    private static final FiguraBackendDealer backend = new FiguraBackendDealer();
    private static final FiguraDevelopmentBackendDealer devBackend = new FiguraDevelopmentBackendDealer();

    private static final HashMap<UUID, FiguraMetadata> METADATA = new HashMap<>();

    private static final boolean useDeveloperBackend = false;


    // -- Functions -- //

    public static void init() {
        //Register a function that will clear all events from dealers when you leave a server.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            for (FiguraDealer dealer : registeredDealers)
                dealer.clearRequests();
            METADATA.clear();
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

    public static FiguraMetadata getMetadata(UUID targetID) {
        //try getting a cached metadata
        FiguraMetadata metadata = METADATA.get(targetID);
        if (metadata == null) {
            //if metadata does not exist on cache, create a new one
            FiguraMetadata newMetadata = new FiguraMetadata(targetID);

            //cache and return the new metadata
            METADATA.put(targetID, newMetadata);
            return newMetadata;
        } else {
            //return cached metadata
            return metadata;
        }
    }

    public static FiguraMetadata getMetadata(Entity entity) {
        var md = getMetadata(entity.getUuid());

        if (entity instanceof PlayerEntity)
            md.setTrustContainer(TrustManager.get(entity.getUuid()));
        return md;
    }


    public static FiguraBackendDealer getBackend() {
        return (FabricLoader.getInstance().isDevelopmentEnvironment() && useDeveloperBackend) ? devBackend : backend;
    }
}
