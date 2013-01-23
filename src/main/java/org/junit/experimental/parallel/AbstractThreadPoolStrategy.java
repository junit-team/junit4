package org.junit.experimental.parallel;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Abstract parallel scheduling strategy in private package.
 * The remaining abstract methods have to be implemented differently
 * depending if the thread pool is shared with other strategies or not.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 *
 * @see SchedulingStrategy
 * @see SharedThreadPoolStrategy
 * @see NonSharedThreadPoolStrategy
 */
abstract class AbstractThreadPoolStrategy extends SchedulingStrategy {
    private final ExecutorService threadPool;
    private final Collection<Future<?>> futureResults;
    private volatile boolean canSchedule;

    AbstractThreadPoolStrategy(ExecutorService threadPool) {
        this(threadPool, null);
    }

    AbstractThreadPoolStrategy(ExecutorService threadPool, Collection<Future<?>> futureResults) {
        canSchedule = true;
        this.threadPool = threadPool;
        this.futureResults = futureResults;
    }

    protected final ExecutorService getThreadPool() {
        return threadPool;
    }

    protected final Collection<Future<?>> getFutureResults() {
        return futureResults;
    }

    protected final void disable() {
        canSchedule = false;
    }

    @Override
    public void schedule(Runnable task) {
        if (canSchedule()) {
            Future<?> futureResult = threadPool.submit(task);
            if (futureResults != null) {
                futureResults.add(futureResult);
            }
        }
    }

    @Override
    protected boolean stop() {
        canSchedule = false;
        if (threadPool.isShutdown()) {
            return false;
        } else {
            threadPool.shutdown();
            return true;
        }
    }

    @Override
    protected boolean stopNow() {
        canSchedule = false;
        if (threadPool.isShutdown()) {
            return false;
        } else {
            threadPool.shutdownNow();
            return true;
        }
    }

    @Override
    public final boolean canSchedule() {
        return canSchedule;
    }
}