package org.junit.experimental.parallel;

import java.util.concurrent.Semaphore;

/**
 * The Balancer controls the maximum of concurrent threads in the current Scheduler(s) and prevents
 * from own thread resources exhaustion if other group of schedulers share the same pool of threads.
 * <p>
 * If a permit is available, {@link #acquirePermit()} simply returns and a new test is scheduled
 * by {@link Scheduler#schedule(Runnable)} in the current runner. Otherwise waiting for a release.
 * One permit is released as soon as the child thread has finished.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class Balancer {
    private final Semaphore balancer;
    private final int maxPermits;

    /**
     * Infinite permits.
     */
    protected Balancer() {
        this(0);
    }

    /**
     * <tt>fair</tt> set to false.
     * @see #Balancer(int, boolean)
     */
    public Balancer(int numPermits) {
        this(numPermits, false);
    }

    /**
     * @param numPermits number of permits to acquire when maintaining concurrency on tests
     * @param fair <tt>true</tt> guarantees the waiting schedulers to wake up in order they acquired a permit
     */
    public Balancer(int numPermits, boolean fair) {
        boolean maintainTests = numPermits > 0 && numPermits < Integer.MAX_VALUE;
        balancer = maintainTests ? new Semaphore(numPermits, fair) : null;
        maxPermits = numPermits;
    }

    /**
     * Acquires a permit from this balancer, blocking until one is available.
     *
     * @return <code>true</code> if current thread is <em>NOT</em> interrupted
     *         while waiting for a permit.
     */
    public boolean acquirePermit() {
        Semaphore balancer = this.balancer;
        if (balancer != null) {
            try {
                balancer.acquire();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Releases a permit, returning it to the balancer.
     */
    public void releasePermit() {
        Semaphore balancer = this.balancer;
        if (balancer != null) {
            balancer.release();
        }
    }

    public void releaseAllPermits() {
        Semaphore balancer = this.balancer;
        if (balancer != null) {
            balancer.release(maxPermits);
        }
    }
}
