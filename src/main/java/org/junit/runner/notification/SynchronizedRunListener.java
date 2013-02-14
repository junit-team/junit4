package org.junit.runner.notification;

import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * Thread-safe decorator for {@link RunListener} implementations that synchronizes
 * calls to the delegate.
 *
 * @author Tibor Digana (tibor17)
 * @author Kevin Cooney (kcooney)
 * @since 4.12
 *
 * @see RunNotifier
 */
@RunListener.ThreadSafe
final class SynchronizedRunListener extends RunListener {
    private static final Object sMonitor = new Object();
    private final RunListener fListener;

    /**
     * Wraps the given listener with {@link SynchronizedRunListener} if
     * it is not annotated with {@link RunListener.ThreadSafe}.
     */
    public static RunListener wrapIfNotThreadSafe(RunListener listener) {
        return listener.getClass().isAnnotationPresent(RunListener.ThreadSafe.class) ?
                listener : new SynchronizedRunListener(listener);
    }

    SynchronizedRunListener(RunListener listener) {
        this.fListener = listener;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        synchronized (sMonitor) {
            fListener.testRunStarted(description);
        }
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        synchronized (sMonitor) {
            fListener.testRunFinished(result);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        synchronized (sMonitor) {
            fListener.testStarted(description);
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        synchronized (sMonitor) {
            fListener.testFinished(description);
        }
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        synchronized (sMonitor) {
            fListener.testFailure(failure);
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        synchronized (sMonitor) {
            fListener.testAssumptionFailure(failure);
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        synchronized (sMonitor) {
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
        SynchronizedRunListener that= (SynchronizedRunListener) other;
        
        return this.fListener.equals(that.fListener);
    }

    @Override
    public String toString() {
        return fListener.toString();
    }
}
