package org.junit.runner.notification;

import net.jcip.annotations.ThreadSafe;
import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * SynchronizedRunListener decorates {@link RunListener} and
 * has all methods synchronized.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
@ThreadSafe
final class SynchronizedRunListener extends RunListener {
    private final RunListener listener;

    SynchronizedRunListener(RunListener listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void testRunStarted(Description description) throws Exception {
        listener.testRunStarted(description);
    }

    @Override
    public synchronized void testRunFinished(Result result) throws Exception {
        listener.testRunFinished(result);
    }

    @Override
    public synchronized void testStarted(Description description) throws Exception {
        listener.testStarted(description);
    }

    @Override
    public synchronized void testFinished(Description description) throws Exception {
        listener.testFinished(description);
    }

    @Override
    public synchronized void testFailure(Failure failure) throws Exception {
        listener.testFailure(failure);
    }

    @Override
    public synchronized void testAssumptionFailure(Failure failure) {
        listener.testAssumptionFailure(failure);
    }

    @Override
    public synchronized void testIgnored(Description description) throws Exception {
        listener.testIgnored(description);
    }

    @Override
    public int hashCode() {
        return listener.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o instanceof SynchronizedRunListener) {
            SynchronizedRunListener other = (SynchronizedRunListener) o;
            return listener.equals(other.listener);
        } else {
            return listener.equals(o);
        }
    }

    @Override
    public String toString() {
        return listener.toString();
    }
}
