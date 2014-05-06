package org.junit.runner.notification;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.testsupport.EventCollectorMatchers.hasNoFailure;
import static org.junit.testsupport.EventCollectorMatchers.hasNumberOfTestsStarted;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.testsupport.EventCollector;

public class RunNotifierTest {
    private final RunNotifier fNotifier = new RunNotifier();

    @Test
    public void notifiesSecondListenerIfFirstThrowsException() {
        EventCollector eventCollector = new EventCollector();
        fNotifier.addListener(new CorruptListener());
        fNotifier.addListener(eventCollector);
        fNotifier.fireTestFailure(new Failure(null, null));
        assertThat(eventCollector, not(hasNoFailure()));
    }

    @Test
    public void hasNoProblemsWithFailingListeners() { // see issues 209 and 395
        fNotifier.addListener(new CorruptListener());
        fNotifier.addListener(new EventCollector());
        fNotifier.addListener(new CorruptListener());
        fNotifier.fireTestRunFinished(new Result());
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

    @Test
    public void addAndRemoveWithNonThreadSafeListener() {
        EventCollector listener = new EventCollector();
        assertThat(listener, hasNumberOfTestsStarted(0));
        fNotifier.addListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
    }

    @Test
    public void addFirstAndRemoveWithNonThreadSafeListener() {
        EventCollector listener = new EventCollector();
        assertThat(listener, hasNumberOfTestsStarted(0));
        fNotifier.addFirstListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
    }

    @Test
    public void addAndRemoveWithThreadSafeListener() {
        ThreadSafeListener listener = new ThreadSafeListener();
        assertThat(listener, hasNumberOfTestsStarted(0));
        fNotifier.addListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
    }

    @Test
    public void addFirstAndRemoveWithThreadSafeListener() {
        ThreadSafeListener listener = new ThreadSafeListener();
        assertThat(listener, hasNumberOfTestsStarted(0));
        fNotifier.addFirstListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
        fNotifier.removeListener(listener);
        fNotifier.fireTestStarted(null);
        assertThat(listener, hasNumberOfTestsStarted(1));
    }

    @Test
    public void wrapIfNotThreadSafeShouldNotWrapThreadSafeListeners() {
        ThreadSafeListener listener = new ThreadSafeListener();
        assertSame(listener, new RunNotifier().wrapIfNotThreadSafe(listener));
    }

    @Test
    public void wrapIfNotThreadSafeShouldWrapNonThreadSafeListeners() {
        EventCollector listener = new EventCollector();
        RunListener wrappedListener = new RunNotifier().wrapIfNotThreadSafe(listener);
        assertThat(wrappedListener, instanceOf(SynchronizedRunListener.class));
    }

    @RunListener.ThreadSafe
    private static class ThreadSafeListener extends EventCollector {
    }

}
