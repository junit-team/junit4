package org.junit.runner.notification;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Testing RunNotifier with concurrent access.
 * <p/>
 *
 * @author tibor17
 * @since 4.11
 */
public final class ConcurrentRunNotifierTest {
    private static final long TIMEOUT= 15;

    private static class ConcurrentRunListener extends RunListener {
        final AtomicInteger testRunStarted= new AtomicInteger(0);
        final AtomicInteger testRunFinished= new AtomicInteger(0);
        final AtomicInteger testStarted= new AtomicInteger(0);
        final AtomicInteger testFinished= new AtomicInteger(0);
        final AtomicInteger testFailure= new AtomicInteger(0);
        final AtomicInteger testAssumptionFailure= new AtomicInteger(0);
        final AtomicInteger testIgnored= new AtomicInteger(0);

        public void testRunStarted(Description description) throws Exception {
            testRunStarted.incrementAndGet();
        }

        public void testRunFinished(Result result) throws Exception {
            testRunFinished.incrementAndGet();
        }

        public void testStarted(Description description) throws Exception {
            testStarted.incrementAndGet();
        }

        public void testFinished(Description description) throws Exception {
            testFinished.incrementAndGet();
        }

        public void testFailure(Failure failure) throws Exception {
            testFailure.incrementAndGet();
        }

        public void testAssumptionFailure(Failure failure) {
            testAssumptionFailure.incrementAndGet();
        }

        public void testIgnored(Description description) throws Exception {
            testIgnored.incrementAndGet();
        }
    }

    @Test
    public void countDuplicatedListeners() throws InterruptedException {
        final int listenerCountPerEvent = 10;
        final ConcurrentRunListener examinedListener = new ConcurrentRunListener();
        final RunNotifier notifier = new RunNotifier();
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < listenerCountPerEvent; ++i) {
            pool.submit(new Runnable() {
                public void run() {
                    notifier.addListener(examinedListener);
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));
        notifier.fireTestRunStarted(null);
        notifier.fireTestRunFinished(null);
        notifier.fireTestStarted(null);
        notifier.fireTestFinished(null);
        notifier.fireTestFailure(null);
        notifier.fireTestAssumptionFailed(null);
        notifier.fireTestIgnored(null);
        assertThat(examinedListener.testRunStarted.get(), is(listenerCountPerEvent));
        assertThat(examinedListener.testRunFinished.get(), is(listenerCountPerEvent));
        assertThat(examinedListener.testStarted.get(), is(listenerCountPerEvent));
        assertThat(examinedListener.testFinished.get(), is(listenerCountPerEvent));
        assertThat(examinedListener.testFailure.get(), is(listenerCountPerEvent));
        assertThat(examinedListener.testAssumptionFailure.get(), is(listenerCountPerEvent));
        assertThat(examinedListener.testIgnored.get(), is(listenerCountPerEvent));
    }

    @Test
    public void countUniqueListeners() throws InterruptedException {
        final ConcurrentRunListener[] examinedListeners = new ConcurrentRunListener[10];
        for (int i = 0; i < examinedListeners.length; ++i) {
            examinedListeners[i] = new ConcurrentRunListener();
        }
        final RunNotifier notifier = new RunNotifier();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.submit(new Runnable() {
            public void run() {
                for (ConcurrentRunListener examinedListener : examinedListeners) {
                    notifier.addListener(examinedListener);
                }
            }
        });
        pool.shutdown();
        assertTrue(pool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));

        notifier.fireTestRunStarted(null);
        notifier.fireTestRunStarted(null);

        notifier.fireTestRunFinished(null);
        notifier.fireTestRunFinished(null);

        notifier.fireTestStarted(null);
        notifier.fireTestStarted(null);

        notifier.fireTestFinished(null);
        notifier.fireTestFinished(null);

        notifier.fireTestFailure(null);
        notifier.fireTestFailure(null);

        notifier.fireTestAssumptionFailed(null);
        notifier.fireTestAssumptionFailed(null);

        notifier.fireTestIgnored(null);
        notifier.fireTestIgnored(null);

        for (ConcurrentRunListener examinedListener : examinedListeners) {
            assertThat(examinedListener.testRunStarted.get(), is(2));
        }

        for (ConcurrentRunListener examinedListener : examinedListeners) {
            assertThat(examinedListener.testRunFinished.get(), is(2));
        }

        for (ConcurrentRunListener examinedListener : examinedListeners) {
            assertThat(examinedListener.testStarted.get(), is(2));
        }

        for (ConcurrentRunListener examinedListener : examinedListeners) {
            assertThat(examinedListener.testFinished.get(), is(2));
        }

        for (ConcurrentRunListener examinedListener : examinedListeners) {
            assertThat(examinedListener.testFailure.get(), is(2));
        }

        for (ConcurrentRunListener examinedListener : examinedListeners) {
            assertThat(examinedListener.testAssumptionFailure.get(), is(2));
        }

        for (ConcurrentRunListener examinedListener : examinedListeners) {
            assertThat(examinedListener.testIgnored.get(), is(2));
        }
    }

    private static class ExaminedListener extends RunListener {
        final AtomicBoolean testRunStarted= new AtomicBoolean(false);
        volatile boolean useMe = false;
        volatile boolean hasTestFailure = false;

        ExaminedListener(boolean useMe) {
            this.useMe = useMe;
        }

        public void testRunStarted(Description description) throws Exception {
            testRunStarted.set(true);
            if (!useMe) throw new Exception();
        }

        public void testRunFinished(Result result) throws Exception {
            if (!useMe) throw new Exception();
        }

        public void testStarted(Description description) throws Exception {
            if (!useMe) throw new Exception();
        }

        public void testFinished(Description description) throws Exception {
            if (!useMe) throw new Exception();
        }

        public void testFailure(Failure failure) throws Exception {
            hasTestFailure = true;
        }

        public void testAssumptionFailure(Failure failure) {
            if (!useMe) throw new RuntimeException();
        }

        public void testIgnored(Description description) throws Exception {
            if (!useMe) throw new Exception();
        }
    }

    @Test @SuppressWarnings("unchecked")
    public void reportConcurrentFailuresAfterAddListener() throws InterruptedException {
        final RunNotifier notifier = new RunNotifier();

        int totalListenersFailures = 0;

        final ExaminedListener[] examinedListeners = new ExaminedListener[(int) 1E3];
        for (int i = 0; i < examinedListeners.length; ++i) {
            boolean fail = StrictMath.random() >= 0.5d;
            if (fail) ++totalListenersFailures;
            examinedListeners[i] = new ExaminedListener(!fail);
        }

        final CyclicBarrier trigger = new CyclicBarrier(2);
        final AtomicBoolean condition = new AtomicBoolean(true);

        ExecutorService callbacksPool = Executors.newSingleThreadExecutor();
        callbacksPool.submit(new Callable() {
            public Object call() throws Exception {
                trigger.await();
                while (condition.get()) {
                    notifier.fireTestStarted(null);
                }
                notifier.fireTestStarted(null);
                return null;
            }
        });

        ExecutorService listenersPool = Executors.newSingleThreadExecutor();
        listenersPool.submit(new Callable() {
            public Object call() throws Exception {
                trigger.await();
                for (ExaminedListener examinedListener : examinedListeners) {
                    notifier.addListener(examinedListener);
                }
                return null;
            }
        });

        callbacksPool.shutdown();
        listenersPool.shutdown();

        assertTrue(listenersPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));
        condition.set(false);
        assertTrue(callbacksPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));

        if (totalListenersFailures != 0) {
            // If no listener failures, then all the listeners do not report any failure.
            int countTestFailures = examinedListeners.length - countReportedTestFailures(examinedListeners);
            assertThat(totalListenersFailures, is(countTestFailures));
        }
    }

    @Test @SuppressWarnings("unchecked")
    public void reportConcurrentFailuresAfterAddFirstListener() throws InterruptedException {
        final RunNotifier notifier = new RunNotifier();

        int totalListenersFailures = 0;

        final ExaminedListener[] examinedListeners = new ExaminedListener[(int) 1E2];
        for (int i = 0; i < examinedListeners.length; ++i) {
            boolean fail = StrictMath.random() >= 0.5d;
            if (fail) ++totalListenersFailures;
            examinedListeners[i] = new ExaminedListener(!fail);
        }

        final CyclicBarrier trigger = new CyclicBarrier(2);
        final AtomicBoolean condition = new AtomicBoolean(true);

        ExecutorService callbacksPool = Executors.newSingleThreadExecutor();
        callbacksPool.submit(new Callable() {
            public Object call() throws Exception {
                trigger.await();
                while (condition.get()) {
                    notifier.fireTestStarted(null);
                }
                notifier.fireTestStarted(null);
                return null;
            }
        });

        ExecutorService listenersPool = Executors.newSingleThreadExecutor();
        listenersPool.submit(new Callable() {
            public Object call() throws Exception {
                trigger.await();
                for (ExaminedListener examinedListener : examinedListeners) {
                    notifier.addFirstListener(examinedListener);
                }
                return null;
            }
        });

        callbacksPool.shutdown();
        listenersPool.shutdown();

        assertTrue(listenersPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));
        condition.set(false);
        assertTrue(callbacksPool.awaitTermination(TIMEOUT, TimeUnit.SECONDS));

        if (totalListenersFailures != 0) {
            // If no listener failures, then all the listeners do not report any failure.
            int countTestFailures = examinedListeners.length - countReportedTestFailures(examinedListeners);
            assertThat(totalListenersFailures, is(countTestFailures));
        }
    }

    private static int countReportedTestFailures(ExaminedListener[] listeners) {
        int count= 0;
        for (ExaminedListener listener : listeners)
            if (listener.hasTestFailure) ++count;
        return count;
    }
}
