package server.services.threads;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

public abstract class AsyncResponseThread extends Thread{
    private AsyncResponse response;

    public AsyncResponseThread(AsyncResponse response){
        this.response=response;
    }

    @Override
    public void run() {
        Response result = null;
        result = operation();
        response.resume(result);
    }

    protected abstract Response operation();

}
