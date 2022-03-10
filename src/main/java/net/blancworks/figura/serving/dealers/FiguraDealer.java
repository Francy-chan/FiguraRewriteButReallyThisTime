package net.blancworks.figura.serving.dealers;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.serving.dealers.backend.requests.DealerRequest;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.lang.ref.Cleaner;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * This class is responsible for creating and providing avatar cards from storage.
 * <p>
 * Provides, on request, a set of avatars for a given user's UUID asynchronously
 */
public abstract class FiguraDealer {

    // -- Variables -- //
    public static final int MAX_AVATARS = 4;

    //This is a map of ALL avatar lists for ALL entities.
    private final HashMap<UUID, AvatarGroup> allAvatarGroups = new HashMap<>();

    protected final DealerRequest[] activeRequests = new DealerRequest[4];
    protected final Queue<DealerRequest> requestQueue = new ConcurrentLinkedQueue<>();

    // -- Functions -- //

    public abstract Identifier getID();

    /**
     * Gets a new AvatarHolder for the given UUID, raw.
     */
    public abstract AvatarHolder getHolder(UUID id);

    /**
     * Called once per game tick.
     */
    public void tick() {

        // - Process requests - //

        for (int i = 0; i < activeRequests.length; i++) {
            DealerRequest request = activeRequests[i];

            //Remove null requests
            if (request != null && request.isFinished) {
                activeRequests[i] = null;
                continue;
            }

            //Pull next request if possible
            if (request == null) {
                DealerRequest newRequest = requestQueue.poll();

                //If there was no request, break the loop, as there are no requests this tick.
                if (newRequest == null) break;

                //Put new request into request array
                activeRequests[i] = newRequest;
                newRequest.submitRequest();
            }
        }

    }

    /**
     * Clears all requests from the dealer.
     * Used when the player exits a server, so we don't keep making requests once we're disconnected.
     */
    public void clearRequests() {
        requestQueue.clear();
        for (Map.Entry<UUID, AvatarGroup> entry : allAvatarGroups.entrySet())
            entry.getValue().destroyGroup();

        allAvatarGroups.clear();
    }

    protected synchronized AvatarGroup getOrCreateGroup(UUID id) {
        return allAvatarGroups.computeIfAbsent(id, this::constructNewGroup);
    }

    protected synchronized void removeGroup(UUID id) {
        allAvatarGroups.remove(id);
    }

    protected AvatarGroup constructNewGroup(UUID id) {
        FiguraMod.LOGGER.info("Constructing avatar group for entity ID " + id);
        return new AvatarGroup(id, this::removeGroup);
    }

    // -- Nested types -- //

    /**
     * Manages an array of figura avatars owned by this dealer, handles the reference counting of AvatarHolders.
     */
    protected static class AvatarGroup {
        // -- Variables -- //
        //Used to clean up AvatarHolders once other things have stopped referencing them.
        private static final Cleaner holderCleaner = Cleaner.create();

        //The ID of the entity that created this group.
        private final UUID sourceID;
        private final Consumer<UUID> removeEvent;

        //List of avatars in this group.
        private final FiguraAvatar[] avatars = new FiguraAvatar[MAX_AVATARS];

        //Manual clear
        private boolean isDestroyed = false;

        //private final List<AvatarHolder> allHolders = new ArrayList<>();

        //Number of references to this group.
        private int refCount = 0;

        // -- Constructors -- //

        public AvatarGroup(UUID id, Consumer<UUID> removeEvent) {
            this.sourceID = id;
            this.removeEvent = removeEvent;
        }

        // -- Functions -- //

        //Obtains an AvatarHolder that references the avatars in this group.
        public synchronized AvatarHolder getHolder() {
            AvatarHolder newHolder = new AvatarHolder(avatars);
            //allHolders.add(newHolder);

            refCount++;
            holderCleaner.register(newHolder, this::onHolderCleanup);

            FiguraMod.LOGGER.info("Holder created " + sourceID + "|" + refCount);

            return newHolder;
        }

        //Called whenever an AvatarHolder is GC'd.
        private synchronized void onHolderCleanup() {
            if (isDestroyed) return;

            refCount--;

            FiguraMod.LOGGER.info("Holder cleaned up " + sourceID + "|" + refCount);

            //If this was the last avatar holder for these avatars,
            //then we know this group is no longer in use anywhere in memory, so destroy it.
            if (refCount <= 0)
                destroyGroup();
        }

        private void destroyGroup() {
            if (isDestroyed) return;
            isDestroyed = true;

            removeEvent.accept(sourceID);
        }
    }

}
