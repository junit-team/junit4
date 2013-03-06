package org.junit.runner.notification;

import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * Thread-safe decorator for {@link RunListener} implementations that synchronizes
 * calls to the delegate.
 *
 * <p>This class synchronizes all listener calls on a RunNotifier instance. This is done because
 * prior to JUnit 4.12, all listeners were called in a synchronized block in RunNotifier,
 * so no two listeners were ever called concurrently. If we instead made the methods here
 * sychronized, clients that added multiple listeners that called common code might see
 * issues due to the reduced synchronization.
 *
 * @author Tibor Digana (tibor17)
 * @author Kevin Cooney (kcooney)
 * @since 4.12
 *
 * @see RunNotifier
 */
@RunListener.ThreadSafe
final class SynchronizedRunListener extends RunListener {
    private final RunListener fListener;
    private final Object fMonitor;

    SynchronizedRunListener(RunListener listener, Object monitor) {
        fListener = listener;
        fMonitor = monitor;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        synchronized (fMonitor) {
            fListener.testRunStarted(description);
        }
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        synchronized (fMonitor) {
            fListener.testRunFinished(result);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        synchronized (fMonitor) {
            fListener.testStarted(description);
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        synchronized (fMonitor) {
            fListener.testFinished(description);
        }
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        synchronized (fMonitor) {
            fListener.testFailure(failure);
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        synchronized (fMonitor) {
            fListener.testAssumptionFailure(failure);
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        synchronized (fMonitor) {
            fListener.testIgnored(description);
        }
    }

    @Override
    public int hashCode() {
        return fListener.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SynchronizedRunListener)) {
            return false;
        }
        SynchronizedRunListener that = (SynchronizedRunListener) other;
        
        return fListener.equals(that.fListener);
    }

    @Override
    public String toString() {
        return fListener.toString() + " (with synchronization wrapper)";
    }
}
