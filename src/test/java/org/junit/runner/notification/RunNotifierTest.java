package org.junit.runner.notification;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;

public class RunNotifierTest {
    private final RunNotifier notifier = new RunNotifier();

    @Test
    public void notifiesSecondListenerIfFirstThrowsException() {
        FailureListener failureListener = new FailureListener();
        notifier.addListener(new CorruptListener());
        notifier.addListener(failureListener);
        notifier.fireTestFailure(new Failure(null, null));
        assertNotNull("The FailureListener registered no failure.",
                failureListener.failure);
    }

    @Test
    public void hasNoProblemsWithFailingListeners() { // see issues 209 and 395
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
    
    @Test
    public void addAndRemoveWithNonThreadSafeListener() {
        CountingListener listener = new CountingListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @Test
    public void addFirstAndRemoveWithNonThreadSafeListener() {
        CountingListener listener = new CountingListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addFirstListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }
    
    @Test
    public void addAndRemoveWithThreadSafeListener() {
        ThreadSafeListener listener = new ThreadSafeListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @Test
    public void addFirstAndRemoveWithThreadSafeListener() {
        ThreadSafeListener listener = new ThreadSafeListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addFirstListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @Test
    public void wrapIfNotThreadSafeShouldNotWrapThreadSafeListeners() {
        ThreadSafeListener listener = new ThreadSafeListener();;
        assertSame(listener, new RunNotifier().wrapIfNotThreadSafe(listener));
    }

    @Test
    public void wrapIfNotThreadSafeShouldWrapNonThreadSafeListeners() {
        CountingListener listener = new CountingListener();
        RunListener wrappedListener = new RunNotifier().wrapIfNotThreadSafe(listener);
        assertThat(wrappedListener, instanceOf(SynchronizedRunListener.class));
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
    
    @RunListener.ThreadSafe
    private static class ThreadSafeListener extends CountingListener {
    }

}
