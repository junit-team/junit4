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
 * synchronized, clients that added multiple listeners that called common code might see
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
    private final RunListener listener;
    private final Object monitor;

    SynchronizedRunListener(RunListener listener, Object monitor) {
        this.listener = listener;
        this.monitor = monitor;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        synchronized (monitor) {
            listener.testRunStarted(description);
        }
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        synchronized (monitor) {
            listener.testRunFinished(result);
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Synchronized decorator for {@link RunListener#testSuiteStarted(Description)}.
     * @param description the description of the test suite that is about to be run
     *                    (generally a class name).
     * @throws Exception if any occurs.
     * @since 4.13
     */
    @Override
    public void testSuiteStarted(Description description) throws Exception {
        synchronized (monitor) {
            listener.testSuiteStarted(description);
        }
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Synchronized decorator for {@link RunListener#testSuiteFinished(Description)}.
     * @param description the description of the test suite that just ran.
     * @since 4.13
     */
    @Override
    public void testSuiteFinished(Description description) throws Exception {
        synchronized (monitor) {
            listener.testSuiteFinished(description);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        synchronized (monitor) {
            listener.testStarted(description);
        }
    }

    @Override
    public void testFinished(Description description) throws Exception {
        synchronized (monitor) {
            listener.testFinished(description);
        }
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        synchronized (monitor) {
            listener.testFailure(failure);
        }
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        synchronized (monitor) {
            listener.testAssumptionFailure(failure);
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        synchronized (monitor) {
            listener.testIgnored(description);
        }
    }

    @Override
    public int hashCode() {
        return listener.hashCode();
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
        
        return listener.equals(that.listener);
    }

    @Override
    public String toString() {
        return listener.toString() + " (with synchronization wrapper)";
    }
}
