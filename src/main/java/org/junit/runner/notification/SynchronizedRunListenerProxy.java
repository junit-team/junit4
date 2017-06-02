package org.junit.runner.notification;

import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * Adds synchronization to a run listener, to facilitate non-threadsafe run listeners in a threading environment.
 *
 * @author Kristian Rosenvold
 */
public class SynchronizedRunListenerProxy {
    private final RunListener target;

    public SynchronizedRunListenerProxy(RunListener target) {
        this.target = target;
    }

    public synchronized void testRunStarted(Description description)
            throws Exception {
        target.testRunStarted(description);
    }

    public synchronized void testRunFinished(Result result)
            throws Exception {
        target.testRunFinished(result);
    }

    public synchronized void testStarted(Description description)
            throws Exception {
        target.testStarted(description);
    }

    public synchronized void testFinished(Description description)
            throws Exception {
        target.testFinished(description);
    }

    public synchronized void testFailure(Failure failure)
            throws Exception {
        target.testFailure(failure);
    }

    public synchronized void testAssumptionFailure(Failure failure) {
        target.testAssumptionFailure(failure);
    }

    public synchronized void testIgnored(Description description)
            throws Exception {
        target.testIgnored(description);
    }
}
