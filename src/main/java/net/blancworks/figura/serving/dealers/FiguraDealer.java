package net.blancworks.figura.serving.dealers;

import net.blancworks.figura.serving.entity.AvatarGroup;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is responsible for creating and providing avatar cards from storage.
 * <p>
 * Provides, on request, a set of avatars for a given user's UUID asynchronously
 */
public abstract class FiguraDealer {

    // -- Variables -- //
    protected final DealerRequest[] activeRequests = new DealerRequest[4];

    private final Queue<DealerRequest> requestQueue = new ConcurrentLinkedQueue<>();

    // -- Functions -- //

    public abstract Identifier getID();

    /**
     * Gets a new AvatarGroup that holds the avatars of the given entity.
     */
    public <T extends Entity> AvatarGroup getGroup(T entity) {
        AvatarGroup newGroup = new AvatarGroup();
        requestForEntity(newGroup, entity);
        return newGroup;
    }

    protected abstract <T extends Entity> void requestForEntity(AvatarGroup group, T entity);

    /**
     * Called once per game tick.
     */
    public void tick() {

        // - Process requests - //

        for (int i = 0; i < activeRequests.length; i++) {
            DealerRequest request = activeRequests[i];

            //Pull next request if possible
            if (request == null || request.isFinished) {
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
    }


    protected class DealerRequest {
        // -- Variables -- //
        public boolean isInProgress = false;
        public boolean isFinished = false;

        private final Runnable requestFunction;

        // -- Constructors -- //
        public DealerRequest(Runnable requestFunction) {
            this.requestFunction = requestFunction;
        }

        // -- Functions -- //

        /**
         * Runs the request function, submitting this request to the backend.
         */
        public void submitRequest() {
            if (isInProgress || isFinished) return;
            this.requestFunction.run();
            isInProgress = true;
        }
    }

}
