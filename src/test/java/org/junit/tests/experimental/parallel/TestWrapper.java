package org.junit.tests.experimental.parallel;

import java.util.concurrent.ScheduledExecutorService;
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
    private ScheduledExecutorService fTimer;
    private ScheduledFuture<Boolean> fTimerResult;
    private volatile ParallelComputer fComputer;

    //timer's delay after which shutdown the computer
    private final long fDelay;
    private final TimeUnit fScale;

    TestWrapper(long delay, TimeUnit scale) {
        fDelay= delay;
        fScale= scale;
    }

    abstract boolean isShutdown();

    final void setComputer(ParallelComputer computer) {
        fComputer= computer;
    }

    @Override
    protected void before() {
        scheduleShutdownTimer();
    }

    @Override
    protected void after() {
        assertDelayedShutdownNotDone();
    }

    private void assertDelayedShutdownNotDone() {
        if (fComputer == null) return;
        if (fTimerResult.isDone()) fail();
        if (!isShutdown()) fail("why the test returned without any shutdown?");
        fTimer.shutdownNow();
        try {
            fTimer.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {e.printStackTrace(System.err);}
    }

    /**
     * Releases resources by computer's shutdown after a delay has elapsed
     * when pool#shutdown() could not stop the pool in unit tests.
     * The <tt>fTimerResult#get()</tt> returns <tt>false</tt> if computer
     * is already shutdown by other thread.
     */
    private void scheduleShutdownTimer() {
        fTimer= Executors.newSingleThreadScheduledExecutor();
        fTimerResult= fTimer.schedule(new Callable<Boolean>() {
            public Boolean call() {
                if (isShutdown()) return false;
                if (fComputer != null) {
                    fComputer.shutdown(true);
                }
                return true;
            }
        }, fDelay, fScale);
    }
}
