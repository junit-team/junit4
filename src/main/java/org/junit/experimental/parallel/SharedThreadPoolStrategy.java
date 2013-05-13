package org.junit.experimental.parallel;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Parallel strategy for shared thread pool in private package.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 *
 * @see AbstractThreadPoolStrategy
 */
final class SharedThreadPoolStrategy extends AbstractThreadPoolStrategy {
    SharedThreadPoolStrategy(ExecutorService threadPool) {
        super(threadPool, new ConcurrentLinkedQueue<Future<?>>());
    }

    @Override
    public boolean hasSharedThreadPool() {
        return true;
    }

    @Override
    public boolean finished() throws InterruptedException {
        boolean wasRunningAll = canSchedule();
        for (Future<?> futureResult : getFutureResults()) {
            try {
                futureResult.get();
            } catch (InterruptedException e) {
                // after called external ExecutorService#shutdownNow()
                // or ExecutorService#shutdown()
                wasRunningAll = false;
            } catch (ExecutionException e) {
                // test throws exception
            } catch (CancellationException e) {
                // cannot happen because not calling Future#cancel()
            }
        }
        disable();
        return wasRunningAll;
    }

    @Override
    protected final boolean stop() {
        return stop(false);
    }

    @Override
    protected final boolean stopNow() {
        return stop(true);
    }

    private boolean stop(boolean interrupt) {
        final boolean wasRunning = canSchedule();
        for (Future<?> futureResult : getFutureResults()) {
            futureResult.cancel(interrupt);
        }
        disable();
        return wasRunning;
    }
}