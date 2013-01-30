package org.junit.runner.notification;

import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * See the equals method.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
final class EqualRunListener extends RunListener {
    private final RunListener listener;

    EqualRunListener(RunListener listener) {
        if (listener == null) {
            throw new NullPointerException("null listener");
        }
        this.listener = listener;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        listener.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        listener.testRunFinished(result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        listener.testStarted(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        listener.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        listener.testFailure(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        listener.testAssumptionFailure(failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        listener.testIgnored(description);
    }

    @Override
    public int hashCode() {
        return listener.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && o.equals(listener);
    }

    @Override
    public String toString() {
        return listener.toString();
    }
}
