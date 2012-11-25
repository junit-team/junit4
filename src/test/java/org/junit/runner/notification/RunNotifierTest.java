package org.junit.runner.notification;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.Result;

public class RunNotifierTest {

    @Test
    public void notifiesSecondListenerIfFirstThrowsException() {
        FailureListener failureListener = new FailureListener();
        RunNotifier notifier = new RunNotifier();
        notifier.addListener(new CorruptListener());
        notifier.addListener(failureListener);
        notifier.fireTestFailure(new Failure(null, null));
        assertNotNull("The FailureListener registered no failure.",
                failureListener.failure);
    }

    @Test
    public void hasNoProblemsWithFailingListeners() { // see issues 209 and 395
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
