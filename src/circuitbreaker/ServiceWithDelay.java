package circuitbreaker;

public class ServiceWithDelay {
    private final int delay;

    ServiceWithDelay(int delay) {
        this.delay = delay;
    }

    ServiceWithDelay(){
        this.delay = 60;
    }

    String response(long serverStartTime) {
        long currentTime = System.nanoTime();

        if((currentTime - serverStartTime) * 1.0 /(1000*1000*1000) < delay /*Time it took to recover.*/) {
            return "Delayed service is down";
        } else {
            return "Delayed service is working";
        }
    }
}
