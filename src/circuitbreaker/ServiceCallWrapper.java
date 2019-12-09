package circuitbreaker;

/**
 * Driver class.
 */
public class ServiceCallWrapper {

    public static void main(String[] args) {
        CircuitBreaker circuitBreaker = new CircuitBreaker(
                3000, 1, 500 * 1000 * 1000
        );
        ServiceProxy serviceProxy = new ServiceProxy();
        Long serverStartTime = System.nanoTime();

        int count =0;
        while (true) {
            System.out.println(serviceProxy.remoteResourceResponse(circuitBreaker, serverStartTime));
            System.out.println(circuitBreaker.getState());
            try {
                Thread.sleep(100);
                /**
                 * Below this is all BS. Just to make sure we break out of infinite loop.
                 */
                count++;
                System.out.println(count);
                if(count>100 && circuitBreaker.getState().equals(State.CLOSE.name())) {
                    throw new RuntimeException("breaking the loop");
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
