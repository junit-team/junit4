package org.junit.runner.notification;

import net.jcip.annotations.ThreadSafe;
import org.junit.Test;
import org.junit.runner.Description;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Testing RunNotifier in concurrent access.
 * <p/>
 *
 * @author tibor17
 * @version 4.12
 * @since 4.12
 */
public final class ConcurrentRunNotifierTest {
    private static final long TIMEOUT = 3;

    private static class ConcurrentRunListener extends RunListener {
        final AtomicInteger testStarted = new AtomicInteger(0);

        public void testStarted(Description description) throws Exception {
            testStarted.incrementAndGet();
        }
    }

    @Test
    public void realUsage() throws InterruptedException {
        final RunNotifier notifier = new RunNotifier();

        ConcurrentRunListener listener1 = new ConcurrentRunListener();
        ConcurrentRunListener listener2 = new ConcurrentRunListener();
        notifier.addListener(listener1);
        notifier.addListener(listener2);

        final int numParallelTests = 4;
        ExecutorService pool = Executors.newFixedThreadPool(numParallelTests);
        for (int i = 0; i < numParallelTests; ++i) {
            pool.submit(new Runnable() {
                public void run() {
                    notifier.fireTestStarted(null);
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));

        notifier.removeListener(listener1);
        notifier.removeListener(listener2);

        assertThat(listener1.testStarted.get(), is(4));
        assertThat(listener2.testStarted.get(), is(4));
    }

    private static class ExaminedListener extends RunListener {
        volatile boolean useMe = false;
        volatile boolean hasTestFailure = false;

        ExaminedListener(boolean useMe) {
            this.useMe = useMe;
        }

        public void testStarted(Description description) throws Exception {
            if (!useMe) {
                throw new Exception();
            }
        }

        public void testFailure(Failure failure) throws Exception {
            hasTestFailure = true;
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void reportConcurrentFailuresAfterAddListener() throws InterruptedException, BrokenBarrierException {
        final RunNotifier notifier = new RunNotifier();

        int totalListenersFailures = 0;

        final ExaminedListener[] examinedListeners = new ExaminedListener[1000];
        for (int i = 0; i < examinedListeners.length; ++i) {
            boolean fail = StrictMath.random() >= 0.5d;
            if (fail) {
                ++totalListenersFailures;
            }
            examinedListeners[i] = new ExaminedListener(!fail);
        }

        final CyclicBarrier trigger = new CyclicBarrier(2);
        final AtomicBoolean condition = new AtomicBoolean(true);

        ExecutorService notificationsPool = Executors.newFixedThreadPool(4);
        notificationsPool.submit(new Callable() {
            public Object call() throws Exception {
                trigger.await();
                while (condition.get()) {
                    notifier.fireTestStarted(null);
                }
                notifier.fireTestStarted(null);
                return null;
            }
        });

        trigger.await();

        for (ExaminedListener examinedListener : examinedListeners) {
            notifier.addListener(examinedListener);
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

    @Test
    @SuppressWarnings("unchecked")
    public void reportConcurrentFailuresAfterAddFirstListener() throws InterruptedException, BrokenBarrierException {
        final RunNotifier notifier = new RunNotifier();

        int totalListenersFailures = 0;

        final ExaminedListener[] examinedListeners = new ExaminedListener[1000];
        for (int i = 0; i < examinedListeners.length; ++i) {
            boolean fail = StrictMath.random() >= 0.5d;
            if (fail) {
                ++totalListenersFailures;
            }
            examinedListeners[i] = new ExaminedListener(!fail);
        }

        final CyclicBarrier trigger = new CyclicBarrier(2);
        final AtomicBoolean condition = new AtomicBoolean(true);

        ExecutorService notificationsPool = Executors.newFixedThreadPool(4);
        notificationsPool.submit(new Callable() {
            public Object call() throws Exception {
                trigger.await();
                while (condition.get()) {
                    notifier.fireTestStarted(null);
                }
                notifier.fireTestStarted(null);
                return null;
            }
        });

        trigger.await();

        for (ExaminedListener examinedListener : examinedListeners) {
            notifier.addFirstListener(examinedListener);
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

    @Test
    public void keepContractOnEqualsNegative() {
        RunNotifier notifier = new RunNotifier();
        final ConcurrentRunListener listener = new ConcurrentRunListener();
        ConcurrentRunListener wrappedListener = new ConcurrentRunListener() {
            @Override
            public boolean equals(Object o) {
                return listener.equals(o);
            }
        };
        notifier.addListener(wrappedListener);
        assertThat(wrappedListener.testStarted.get(), is(0));
        notifier.fireTestStarted(null);
        assertThat(wrappedListener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(wrappedListener.testStarted.get(), is(2));
    }

    @Test
    public void keepContractOnEquals() {
        RunNotifier notifier = new RunNotifier();
        final ConcurrentRunListener listener = new ConcurrentRunListener();
        ConcurrentRunListener wrappedListener = new ConcurrentRunListener() {
            @Override
            public boolean equals(Object o) {
                return listener.equals(o);
            }
        };
        notifier.addListener(listener);
        assertThat(listener.testStarted.get(), is(0));
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(wrappedListener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @Test
    public void wrapSynchronizedIfNotThreadSafe() {
        RunNotifier notifier = new RunNotifier();
        ConcurrentRunListener listener = new ConcurrentRunListener();
        notifier.addListener(listener);
        assertThat(listener.testStarted.get(), is(0));
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @ThreadSafe
    private static class ThreadSafeRunListener extends ConcurrentRunListener {
    }

    @Test
    public void doNotWrapIfThreadSafe() {
        RunNotifier notifier = new RunNotifier();
        ThreadSafeRunListener listener = new ThreadSafeRunListener();
        notifier.addListener(listener);
        assertThat(listener.testStarted.get(), is(0));
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
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
