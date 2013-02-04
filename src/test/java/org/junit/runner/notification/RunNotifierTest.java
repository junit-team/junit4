package org.junit.runner.notification;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;
import net.jcip.annotations.ThreadSafe;
import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * Tests for {@link RunNotifier}. These tests are written in JUnit3-style
 * because bugs in {@code RunNotifier} can cause incorrect results when running
 * other tests (including causing failed tests to appear to be passing).
 */
public class RunNotifierTest extends TestCase {
	private RunNotifier notifier;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        notifier = new RunNotifier();
    }

    public void testNotifiesSecondListenerIfFirstThrowsException() {
        FailureListener failureListener = new FailureListener();
        notifier.addListener(new CorruptListener());
        notifier.addListener(failureListener);
        notifier.fireTestFailure(new Failure(null, null));
        assertNotNull("The FailureListener registered no failure.",
                failureListener.failure);
    }

    public void testHasNoProblemsWithFailingListeners() { // see issues 209 and 395
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
    
    public void testAddAndRemoveWithNonThreadSafeListener() {
        CountingListener listener = new CountingListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }
    
    public void testAddFirstAndRemoveWithNonThreadSafeListener() {
        CountingListener listener = new CountingListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addFirstListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }
    
    public void testAddAndRemoveWithThreadSafeListener() {
        ThreadSafeListener listener = new ThreadSafeListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    public void testAddFirstAndRemoveWithThreadSafeListener() {
        ThreadSafeListener listener = new ThreadSafeListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addFirstListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    private static class FailureListener extends RunListener {
        private Failure failure;

        @Override
        public void testFailure(Failure failure) throws Exception {
            this.failure = failure;
        }
    }
    
    private static class CountingListener extends RunListener {
        final AtomicInteger testStarted = new AtomicInteger(0);

        @Override
		public void testStarted(Description description) throws Exception {
            testStarted.incrementAndGet();
        }
    }
    
    @ThreadSafe
    private static class ThreadSafeListener extends CountingListener {
    }

}
