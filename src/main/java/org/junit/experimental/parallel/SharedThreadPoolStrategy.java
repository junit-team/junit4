package org.junit.experimental.parallel;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Parallel strategy for shared thread pool in private package.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 *
 * @see AbstractThreadPoolStrategy
 */
final class SharedThreadPoolStrategy extends AbstractThreadPoolStrategy {
    SharedThreadPoolStrategy(ExecutorService threadPool) {
        super(threadPool, new ArrayList<Future<?>>());
    }

    @Override
    public boolean hasSharedThreadPool() {
        return true;
    }

    @Override
    public boolean finished() throws InterruptedException {
        boolean wasRunningAll = stop();
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
        return wasRunningAll;
    }

    @Override
    protected void awaitStopped() throws InterruptedException {
        finished();
    }

    @Override
    protected final boolean stop() {
        boolean wasStopped = canSchedule();
        disable();
        return wasStopped;
    }

    @Override
    protected final boolean stopNow() {
        return stop();
    }
}