package org.junit.experimental.parallel;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The sequentially executing strategy in private package.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 *
 * @see SchedulingStrategy
 */
final class InvokerStrategy extends SchedulingStrategy {
    private final AtomicBoolean canSchedule = new AtomicBoolean(true);

    @Override
    public void schedule(Runnable task) {
        if (canSchedule()) {
            task.run();
        }
    }

    @Override
    protected boolean stop() {
        return canSchedule.getAndSet(false);
    }

    @Override
    public boolean hasSharedThreadPool() {
        return false;
    }

    @Override
    public boolean canSchedule() {
        return canSchedule.get();
    }

    @Override
    public boolean finished() throws InterruptedException {
        return stop();
    }
}
