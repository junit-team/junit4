package org.junit.experimental.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Parallel strategy for non-shared thread pool in private package.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 *
 * @see AbstractThreadPoolStrategy
 */
final class NonSharedThreadPoolStrategy extends AbstractThreadPoolStrategy {
    NonSharedThreadPoolStrategy(ExecutorService threadPool) {
        super(threadPool);
    }

    @Override
    public boolean hasSharedThreadPool() {
        return false;
    }

    @Override
    public void awaitStopped() throws InterruptedException {
        getThreadPool().awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}
