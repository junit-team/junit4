package org.junit.experimental.parallel;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Skeletal implementation of {@link SchedulingStrategy} in private package.
 * This is parallel scheduling strategy.
 * <p/>
 * If the strategy shares its own thread pool, the abstract method
 * {@link #hasSharedThreadPool()} should return <code>true</code>.
 * <p/>
 * Strategies which do not share their thread pools with other strategies
 * {@link ExecutorService#awaitTermination(long, TimeUnit) await their termination}
 * in method {@link #awaitStopped()} after the stop request {@link #stop()}.
 * Sharing strategies await only own tasks to be terminated in {@link #awaitStopped()}.
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