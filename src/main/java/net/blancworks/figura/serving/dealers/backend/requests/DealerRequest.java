package net.blancworks.figura.serving.dealers.backend.requests;

public class DealerRequest {
    // -- Variables -- //
    public boolean isInProgress = false;
    public boolean isFinished = false;

    // -- Constructors -- //
    public DealerRequest() {
    }

    // -- Functions -- //

    /**
     * Runs the request function, submitting this request to the backend.
     */
    public void submitRequest() {
        if (isInProgress || isFinished) return;
        isInProgress = true;
    }

    protected void onSubmit(){
    }
}