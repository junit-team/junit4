package org.junit.tests.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Helper for unit tests that simulates the behaviour of a test runner to allow for
 * assertions against the outcome of running a test suite.
 */
public class MockTestRunner {
    private TrackingRunListener listener = new TrackingRunListener();
    private RunNotifier notifier = new RunNotifier();
    
    /**
     * Constructor if you want to run tests against the notifier yourself
     */
    public MockTestRunner() {
        notifier.addListener(listener);
    }
    
    /**
     * Factory method - runs the test class against the mock test runner's notifier
     * using {@code BlockJUnit4ClassRunner} as the executor
     * and returns the MockTestRunner
     * @param testClass to test
     * @return new MockTestRunner with notifier that has had the test class run against it
     * @throws InitializationError on initialisation exceptions
     */
    public static MockTestRunner runTestsOf(Class<?> testClass) throws InitializationError {
        MockTestRunner runner = new MockTestRunner();
        new BlockJUnit4ClassRunner(testClass).run(runner.getNotifier());
        return runner;
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
     * @return how many test assumptions failed
     */
    public int getTestAssumptionFailureCount() {
        return listener.testAssumptionFailureCount.get();
    }

    /**
     * @return how many tests were ignored
     */
    public int getTestIgnoredCount() {
        return listener.testIgnoredCount.get();
    }
    
    /**
     * Check that all the named tests were started - will pass if the names
     * mentioned were started. Will not fail if more tests were started than
     * those mentioned. No guarantee of the order of test execution tested for
     */
    public void assertTestsStartedByName(String ... names) {
        assertThat(listener.namesOfStartedTests, hasItems(names));
    }
    
    /**
     * Simple {@link RunListener} that tracks the number of times that
     * certain callbacks are invoked.
     */
    private static class TrackingRunListener extends RunListener {

        final AtomicInteger testStartedCount = new AtomicInteger();
        final AtomicInteger testFailureCount = new AtomicInteger();
        final AtomicInteger testFinishedCount = new AtomicInteger();
        final AtomicInteger testAssumptionFailureCount = new AtomicInteger();
        final AtomicInteger testIgnoredCount = new AtomicInteger();
        
        final Set<String> namesOfStartedTests = new HashSet<String>();

        @Override
        public void testStarted(Description description) throws Exception {
            testStartedCount.incrementAndGet();
            
            synchronized(namesOfStartedTests) {
                namesOfStartedTests.add(description.getMethodName());
            }
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            testFailureCount.incrementAndGet();
        }

        @Override
        public void testFinished(Description description) throws Exception {
            testFinishedCount.incrementAndGet();
        }
        

        @Override
        public void testAssumptionFailure(Failure failure) {
            testAssumptionFailureCount.incrementAndGet();
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            testIgnoredCount.incrementAndGet();
        }
    }
}
