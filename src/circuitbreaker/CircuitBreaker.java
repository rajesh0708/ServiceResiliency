package circuitbreaker;


public class CircuitBreaker {
    private final long timeout;
    private final long retryTimePeriod;
    private final int failureThreshold;
    long lastFailureTime;
    int failureCount;
    private State state;
    private final long futureTime = 1000 * 1000 * 1000 * 1000;


    CircuitBreaker(long timeout, int failureThreshold, long retryTimePeriod) {
        this.timeout = timeout;
        this.retryTimePeriod = retryTimePeriod;
        this.failureThreshold = failureThreshold;
        this.state = State.CLOSE;
        this.lastFailureTime = System.nanoTime() + futureTime;
        failureCount = 0;
    }

    private void reset() {
        failureCount = 0;
        state = State.CLOSE;
        this.lastFailureTime = System.nanoTime() + futureTime;
    }

    protected  void setState() {
        /**
         * TODO: replace this with token bucket so that we can make it more adaptive.
         */
        if(failureCount > failureThreshold) {
            long diffTimeBetweenEvent = System.nanoTime() - lastFailureTime;
            if(diffTimeBetweenEvent > retryTimePeriod) {
                state = State.HALF_OPEN;
            } else {
                state = State.OPEN;
            }

        } else {
            state = State.CLOSE;
        }
    }

    public String getState() {
        return state.name();
    }

    private void recordFailure() {
        failureCount = failureCount + 1;
        this.lastFailureTime = System.nanoTime();
    }

    public String call(String serviceToCall, long serverStartTime) throws Exception {
        /**
         * Set the state. Very first time this will make sure you initialize everything properly.
         */
        setState();
        if("OPEN".equals(state.name())) {
            return "cached data";
        } else {
            if(serviceToCall.equals("delayedService")) {
                ServiceWithDelay delayed = new ServiceWithDelay(20);
                String response = delayed.response(serverStartTime);
                if(response.contains("working")) {
                    reset();
                    return response;
                } else {
                    recordFailure();
                    throw new RuntimeException("Service not responding");
                }
            }
        }
        throw new Exception("Something went wrong");
    }
}
