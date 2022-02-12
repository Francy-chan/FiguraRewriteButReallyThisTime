package net.blancworks.figura.serving.dealers.backend.requests;

public class RunnableDealerRequest extends DealerRequest{
    private final Runnable requestFunction;

    public RunnableDealerRequest(Runnable requestFunction) {
        this.requestFunction = requestFunction;
    }

    @Override
    protected void onSubmit() {
        this.requestFunction.run();
    }
}
