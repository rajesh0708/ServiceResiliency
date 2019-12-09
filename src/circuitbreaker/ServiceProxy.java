package circuitbreaker;

public class ServiceProxy {

    public String remoteResourceResponse(CircuitBreaker circuitBreaker, long serverStartTime) {
        try {
            return circuitBreaker.call("delayedService", serverStartTime);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
