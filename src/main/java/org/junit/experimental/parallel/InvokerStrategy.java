package org.junit.experimental.parallel;

/**
 * The sequentially executing strategy in private package.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 *
 * @see SchedulingStrategy
 */
final class InvokerStrategy extends SchedulingStrategy {
    private volatile boolean canSchedule = true;

    @Override
    public void schedule(Runnable child) {
        if (canScheduleChildren()) {
            child.run();
        }
    }

    @Override
    protected boolean stop() {
        canSchedule = false;
        return true;
    }

    @Override
    protected void awaitStopped() throws InterruptedException {
    }

    @Override
    public boolean hasSharedThreadPool() {
        return false;
    }

    @Override
    public boolean canScheduleChildren() {
        return canSchedule;
    }
}
