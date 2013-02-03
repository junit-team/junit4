package org.junit.runner.notification;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import net.jcip.annotations.ThreadSafe;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
@RunWith(JUnit4.class)
public class AddRemoveListenerTest {

    public static class NormalListener extends RunListener {
        final AtomicInteger testStarted = new AtomicInteger(0);

        @Override
		public void testStarted(Description description) throws Exception {
            testStarted.incrementAndGet();
        }
    }

    @ThreadSafe
    public static class ThreadSafeListener extends RunListener {
        final AtomicInteger testStarted = new AtomicInteger(0);

        @Override
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