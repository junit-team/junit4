package org.junit.runner.notification;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.runner.notification.RunListener.ThreadSafe;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;

import java.util.concurrent.atomic.AtomicInteger;

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

    private static class NormalListener extends RunListener {
        final AtomicInteger testStarted = new AtomicInteger(0);

        public void testStarted(Description description) throws Exception {
            testStarted.incrementAndGet();
        }
    }

    @ThreadSafe
    private static class ThreadSafeListener extends RunListener {
        final AtomicInteger testStarted = new AtomicInteger(0);

        public void testStarted(Description description) throws Exception {
            testStarted.incrementAndGet();
        }
    }

    @Test
    public void keepContractOnEqualsNegative() {
        RunNotifier notifier = new RunNotifier();
        final NormalListener listener = new NormalListener();
        NormalListener wrappedListener = new NormalListener() {
            @Override
            public boolean equals(Object o) {
                return listener.equals(o);
            }
        };
        notifier.addListener(wrappedListener);
        assertThat(wrappedListener.testStarted.get(), is(0));
        notifier.fireTestStarted(null);
        assertThat(wrappedListener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(wrappedListener.testStarted.get(), is(2));
    }

    @Test
    public void keepContractOnEquals() {
        RunNotifier notifier = new RunNotifier();
        final NormalListener listener = new NormalListener();
        NormalListener wrappedListener = new NormalListener() {
            @Override
            public boolean equals(Object o) {
                return listener.equals(o);
            }
        };
        notifier.addListener(listener);
        assertThat(listener.testStarted.get(), is(0));
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(wrappedListener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @Test
    public void addRemoveNormalListener() {
        RunNotifier notifier = new RunNotifier();
        NormalListener listener = new NormalListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @Test
    public void addFirstRemoveNormalListener() {
        RunNotifier notifier = new RunNotifier();
        NormalListener listener = new NormalListener();
        assertThat(listener.testStarted.get(), is(0));
        notifier.addFirstListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
        notifier.removeListener(listener);
        notifier.fireTestStarted(null);
        assertThat(listener.testStarted.get(), is(1));
    }

    @Test
    public void addRemoveThreadSafeListener() {
        RunNotifier notifier = new RunNotifier();
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
    public void addFirstRemoveThreadSafeListener() {
        RunNotifier notifier = new RunNotifier();
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
    public void addRemoveBoth() {
        RunNotifier notifier = new RunNotifier();

        NormalListener normalListener = new NormalListener();
        assertThat(normalListener.testStarted.get(), is(0));
        notifier.addListener(normalListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(1));

        ThreadSafeListener tSafeListener = new ThreadSafeListener();
        assertThat(tSafeListener.testStarted.get(), is(0));
        notifier.addListener(tSafeListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(2));
        assertThat(tSafeListener.testStarted.get(), is(1));

        notifier.removeListener(normalListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(2));
        assertThat(tSafeListener.testStarted.get(), is(2));

        notifier.removeListener(tSafeListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(2));
        assertThat(tSafeListener.testStarted.get(), is(2));
    }

    @Test
    public void addFirstRemoveBoth() {
        RunNotifier notifier = new RunNotifier();

        NormalListener normalListener = new NormalListener();
        assertThat(normalListener.testStarted.get(), is(0));
        notifier.addListener(normalListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(1));

        ThreadSafeListener tSafeListener = new ThreadSafeListener();
        assertThat(tSafeListener.testStarted.get(), is(0));
        notifier.addFirstListener(tSafeListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(2));
        assertThat(tSafeListener.testStarted.get(), is(1));

        notifier.removeListener(normalListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(2));
        assertThat(tSafeListener.testStarted.get(), is(2));

        notifier.removeListener(tSafeListener);
        notifier.fireTestStarted(null);
        assertThat(normalListener.testStarted.get(), is(2));
        assertThat(tSafeListener.testStarted.get(), is(2));
    }
}
