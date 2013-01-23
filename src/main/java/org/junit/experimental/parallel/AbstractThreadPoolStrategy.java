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
abstract class AbstractThreadPoolStrategy<T extends ExecutorService> extends SchedulingStrategy {
    final T threadPool;
    final Collection<Future<?>> futureResults;
    volatile boolean canSchedule;

    AbstractThreadPoolStrategy(T threadPool) {
        this(threadPool, null);
    }

    AbstractThreadPoolStrategy(T threadPool, Collection<Future<?>> futureResults) {
        canSchedule = true;
        this.threadPool = threadPool;
        this.futureResults = futureResults;
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
    public boolean canSchedule() {
        return canSchedule;
    }
}