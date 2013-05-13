package org.junit.experimental.parallel;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract parallel scheduling strategy in private package.
 * The remaining abstract methods have to be implemented differently
 * depending if the thread pool is shared with other strategies or not.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 *
 * @see SchedulingStrategy
 * @see SharedThreadPoolStrategy
 * @see NonSharedThreadPoolStrategy
 */
abstract class AbstractThreadPoolStrategy extends SchedulingStrategy {
    private final ExecutorService threadPool;
    private final Collection<Future<?>> futureResults;
    private final AtomicBoolean canSchedule = new AtomicBoolean(true);

    AbstractThreadPoolStrategy(ExecutorService threadPool) {
        this(threadPool, null);
    }

    AbstractThreadPoolStrategy(ExecutorService threadPool, Collection<Future<?>> futureResults) {
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
        canSchedule.set(false);
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
        boolean wasRunning = canSchedule.getAndSet(false);
        if (threadPool.isShutdown()) {
            wasRunning = false;
        } else {
            threadPool.shutdown();
        }
        return wasRunning;
    }

    @Override
    protected boolean stopNow() {
        boolean wasRunning = canSchedule.getAndSet(false);
        if (threadPool.isShutdown()) {
            wasRunning = false;
        } else {
            threadPool.shutdownNow();
        }
        return wasRunning;
    }

    @Override
    protected void setDefaultShutdownHandler(Scheduler.ShutdownHandler handler) {
        if (threadPool instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor pool = (ThreadPoolExecutor) threadPool;
            handler.setRejectedExecutionHandler(pool.getRejectedExecutionHandler());
            pool.setRejectedExecutionHandler(handler);
        }
    }

    @Override
    public final boolean canSchedule() {
        return canSchedule.get();
    }
}