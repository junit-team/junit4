package org.junit.tests.experimental.parallel;

import org.junit.experimental.ParallelComputer;
import org.junit.rules.ExternalResource;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * @author tibor17
 * @since 4.12
 */
abstract class TestWrapper extends ExternalResource {
    private Runnable fShutdownWatcher;
    private ScheduledFuture<Boolean> fShutdownElapsed;
    private ParallelComputer fComputer;

    //timer's delay after which shutdown the computer
    private final long fDelay;
    private final TimeUnit fScale;

    TestWrapper(long delay, TimeUnit scale) {
        fDelay= delay;
        fScale= scale;
    }

    abstract boolean isShutdown();
    abstract void shutdown();

    final void setComputer(ParallelComputer computer) {
        fComputer= computer;
    }

    @Override
    protected void before() {
        fShutdownElapsed= scheduleShutdownTimer();
    }

    @Override
    protected void after() {
        assertDelayedShutdownNotDone(fShutdownElapsed);
        Runnable shutdownWatcher= fShutdownWatcher;
        fShutdownWatcher= null;
        if (shutdownWatcher != null) shutdownWatcher.run();
    }

    private void assertDelayedShutdownNotDone(ScheduledFuture<Boolean> shutdownTimer) {
        try {
            if (fComputer == null) return;
            if (shutdownTimer.isDone()) fail();
            if (!isShutdown()) fail("why the test returned without any shutdown?");
        } finally {
            cancel(shutdownTimer);
        }
    }

    private void cancel(ScheduledFuture<Boolean> timer) {
        try {
            if (timer.cancel(true)) timer.get();
        } catch (Exception e) {
        }
    }

    /**
     * Releases resources by computer's shutdown after a delay has elapsed
     * when pool#shutdown() could not stop the pool in unit tests.
     *
     * @return {@link java.util.concurrent.ScheduledFuture#get()}
     * <tt>false</tt> if computer is already shutdown by other thread
     */
    private ScheduledFuture<Boolean> scheduleShutdownTimer() {
        return Executors.newSingleThreadScheduledExecutor().schedule(new Callable<Boolean>() {
            public Boolean call() throws InterruptedException {
                try {
                    if (isShutdown()) return false;
                    if (fComputer != null) fComputer.shutdown(true);
                    return true;
                } finally {
                    shutdown();
                }
            }
        }, fDelay, fScale);
    }
}
