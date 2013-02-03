package org.junit.runner.notification;

import junit.framework.TestCase;
import org.junit.runner.Result;

/**
 * Tests for {@link RunNotifier}. These tests are written in JUnit3-style
 * because bugs in {@code RunNotifier} can cause incorrect results when running
 * other tests (including causing failed tests to appear to be passing).
 */
public class RunNotifierTest extends TestCase {

    public void testNotifiesSecondListenerIfFirstThrowsException() {
        FailureListener failureListener = new FailureListener();
        RunNotifier notifier = new RunNotifier();
        notifier.addListener(new CorruptListener());
        notifier.addListener(failureListener);
        notifier.fireTestFailure(new Failure(null, null));
        assertNotNull("The FailureListener registered no failure.",
                failureListener.failure);
    }

    public void testHasNoProblemsWithFailingListeners() { // see issues 209 and 395
        RunNotifier notifier = new RunNotifier();
        notifier.addListener(new CorruptListener());
        notifier.addListener(new FailureListener());
        notifier.addListener(new CorruptListener());
        notifier.fireTestRunFinished(new Result());
    }

    private static class CorruptListener extends RunListener {
        @Override
        public void testRunFinished(Result result) throws Exception {
            throw new RuntimeException();
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            throw new RuntimeException();
        }
    }

    private static class FailureListener extends RunListener {
        private Failure failure;

        @Override
        public void testFailure(Failure failure) throws Exception {
            this.failure = failure;
        }
    }
}
