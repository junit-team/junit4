package org.junit.experimental.parallel;

import java.util.concurrent.Semaphore;

/**
 * If {@link Scheduler} shares own threads with child Scheduler, the Balancer is used to control the peak
 * number of active threads in current Scheduler and prevents from own thread resources exhaustion.
 * <p>
 * If a permit is available, {@link #acquirePermit()} simply returns and a new test is scheduled
 * by {@link Scheduler#schedule(Runnable)} in the current runner. Otherwise waiting for a release.
 * If child runner has finished, one permit is released in its scheduler {@link Scheduler#finished()}.
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
    public Balancer() {
        balancer = null;
        maxPermits = 0;
    }

    /**
     * @param numPermits number of permits to acquire
     * @throws IllegalArgumentException if <tt>numPermits</tt> is not positive
     */
    public Balancer(int numPermits) {
        if (numPermits <= 0) {
            throw new IllegalArgumentException(numPermits + " permits should be positive");
        }
        balancer = new Semaphore(numPermits);
        maxPermits = numPermits;
    }

    public final int getInitialPermits() {
        return maxPermits;
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
