package org.junit.tests.mock;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * Helper for unit tests that simulates the behaviour of a test runner to allow for
 * assertions against the outcome of running a test suite.
 */
public class MockTestRunner {
    private TrackingRunListener listener = new TrackingRunListener();
    private RunNotifier notifier = new RunNotifier();
    
    public MockTestRunner() {
        notifier.addListener(listener);
    }
    
    /**
     * Access the notifier within the mock for using with test runs.
     */
    public RunNotifier getNotifier() {
        return notifier;
    }

    /**
     * @return how many tests were started
     */
    public int getTestStartedCount() {
        return listener.testStartedCount.get();
    }

    /**
     * @return how many tests failed
     */
    public int getTestFailureCount() {
        return listener.testFailureCount.get();
    }
    
    /**
     * @return how many tests finished
     */
    public int getTestFinishedCount() {
        return listener.testFinishedCount.get();
    }
    
    
    /**
     * Simple {@link RunListener} that tracks the number of times that
     * certain callbacks are invoked.
     */
    private static class TrackingRunListener extends RunListener {

        final AtomicInteger testStartedCount = new AtomicInteger();
        final AtomicInteger testFailureCount = new AtomicInteger();
        final AtomicInteger testFinishedCount = new AtomicInteger();


        @Override
        public void testStarted(Description description) throws Exception {
            testStartedCount.incrementAndGet();
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            testFailureCount.incrementAndGet();
        }

        @Override
        public void testFinished(Description description) throws Exception {
            testFinishedCount.incrementAndGet();
        }
    }


}
