package org.junit.runner;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A <code>Result</code> collects and summarizes information from running multiple tests.
 * All tests are counted -- additional information is collected from tests that fail.
 *
 * @since 4.0
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 2L;
    private final AtomicInteger count = new AtomicInteger();
    private final AtomicInteger ignoreCount = new AtomicInteger();
    private final CopyOnWriteArrayList<Failure> failures = new CopyOnWriteArrayList<Failure>();
    private final AtomicLong runTime = new AtomicLong();
    private final AtomicLong startTime = new AtomicLong();

    /**
     * @return the number of tests run
     */
    public int getRunCount() {
        return count.get();
    }

    /**
     * @return the number of tests that failed during the run
     */
    public int getFailureCount() {
        return failures.size();
    }

    /**
     * @return the number of milliseconds it took to run the entire suite to run
     */
    public long getRunTime() {
        return runTime.get();
    }

    /**
     * @return the {@link Failure}s describing tests that failed and the problems they encountered
     */
    public List<Failure> getFailures() {
        return failures;
    }

    /**
     * @return the number of tests ignored during the run
     */
    public int getIgnoreCount() {
        return ignoreCount.get();
    }

    /**
     * @return <code>true</code> if all tests succeeded
     */
    public boolean wasSuccessful() {
        return getFailureCount() == 0;
    }

    @RunListener.ThreadSafe
    private class Listener extends RunListener {
        @Override
        public void testRunStarted(Description description) throws Exception {
            startTime.set(System.currentTimeMillis());
        }

        @Override
        public void testRunFinished(Result result) throws Exception {
            long endTime = System.currentTimeMillis();
            runTime.addAndGet(endTime - startTime.get());
        }

        @Override
        public void testFinished(Description description) throws Exception {
            count.getAndIncrement();
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            failures.add(failure);
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            ignoreCount.getAndIncrement();
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            // do nothing: same as passing (for 4.5; may change in 4.6)
        }
    }

    /**
     * Internal use only.
     */
    public RunListener createListener() {
        return new Listener();
    }
}
