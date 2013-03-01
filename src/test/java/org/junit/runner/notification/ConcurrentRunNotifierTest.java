package org.junit.runner.notification;

import org.junit.Test;
import org.junit.runner.Description;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Testing RunNotifier in concurrent access.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
public final class ConcurrentRunNotifierTest {
    private static final long TIMEOUT = 3;
    private final RunNotifier fNotifier = new RunNotifier();

    private static class ConcurrentRunListener extends RunListener {
        final AtomicInteger fTestStarted = new AtomicInteger(0);

        @Override
        public void testStarted(Description description) throws Exception {
            fTestStarted.incrementAndGet();
        }
    }

    @Test
    public void realUsage() throws Exception {
        ConcurrentRunListener listener1 = new ConcurrentRunListener();
        ConcurrentRunListener listener2 = new ConcurrentRunListener();
        fNotifier.addListener(listener1);
        fNotifier.addListener(listener2);

        final int numParallelTests = 4;
        ExecutorService pool = Executors.newFixedThreadPool(numParallelTests);
        for (int i = 0; i < numParallelTests; ++i) {
            pool.submit(new Runnable() {
                public void run() {
                    fNotifier.fireTestStarted(null);
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));

        fNotifier.removeListener(listener1);
        fNotifier.removeListener(listener2);

        assertThat(listener1.fTestStarted.get(), is(numParallelTests));
        assertThat(listener2.fTestStarted.get(), is(numParallelTests));
    }

    private static class ExaminedListener extends RunListener {
        final boolean throwFromTestStarted;
        volatile boolean hasTestFailure = false;

        ExaminedListener(boolean throwFromTestStarted) {
            this.throwFromTestStarted = throwFromTestStarted;
        }

        @Override
        public void testStarted(Description description) throws Exception {
            if (throwFromTestStarted) {
                throw new Exception();
            }
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            hasTestFailure = true;
        }
    }

    private abstract class AbstractConcurrentFailuresTest {

        protected abstract void addListener(ExaminedListener listener);

        public void test() throws Exception {
            int totalListenersFailures = 0;

            Random random = new Random(42);
            ExaminedListener[] examinedListeners = new ExaminedListener[1000];
            for (int i = 0; i < examinedListeners.length; ++i) {
                boolean fail = random.nextDouble() >= 0.5d;
                if (fail) {
                    ++totalListenersFailures;
                }
                examinedListeners[i] = new ExaminedListener(fail);
            }

            final AtomicBoolean condition = new AtomicBoolean(true);
            final CyclicBarrier trigger = new CyclicBarrier(2);
            final CountDownLatch latch = new CountDownLatch(10);

            ExecutorService notificationsPool = Executors.newFixedThreadPool(4);
            notificationsPool.submit(new Callable<Void>() {
                public Void call() throws Exception {
                    trigger.await();
                    while (condition.get()) {
                        fNotifier.fireTestStarted(null);
                        latch.countDown();
                    }
                    fNotifier.fireTestStarted(null);
                    return null;
                }
            });

            // Wait for callable to start
            trigger.await(TIMEOUT, TimeUnit.SECONDS);

            // Wait for callable to fire a few events
            latch.await(TIMEOUT, TimeUnit.SECONDS);

            for (ExaminedListener examinedListener : examinedListeners) {
              addListener(examinedListener);
            }

            notificationsPool.shutdown();
            condition.set(false);
            assertTrue(notificationsPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));

            if (totalListenersFailures != 0) {
                // If no listener failures, then all the listeners do not report any failure.
                int countTestFailures = examinedListeners.length - countReportedTestFailures(examinedListeners);
                assertThat(totalListenersFailures, is(countTestFailures));
            }
        }
    }

    /**
     * Verifies that listeners added while tests are run concurrently are
     * notified about test failures.
     */
    @Test
    public void reportConcurrentFailuresAfterAddListener() throws Exception {
        new AbstractConcurrentFailuresTest() {
            @Override
            protected void addListener(ExaminedListener listener) {
                fNotifier.addListener(listener);
            }
        }.test();
    }

    /**
     * Verifies that listeners added with addFirstListener() while tests are run concurrently are
     * notified about test failures.
     */
    @Test
    public void reportConcurrentFailuresAfterAddFirstListener() throws Exception {
        new AbstractConcurrentFailuresTest() {
            @Override
            protected void addListener(ExaminedListener listener) {
                fNotifier.addFirstListener(listener);
            }
        }.test();
    }

    private static int countReportedTestFailures(ExaminedListener[] listeners) {
        int count = 0;
        for (ExaminedListener listener : listeners) {
            if (listener.hasTestFailure) {
                ++count;
            }
        }
        return count;
    }
}
