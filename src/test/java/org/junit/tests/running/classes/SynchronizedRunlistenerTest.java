package org.junit.tests.running.classes;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.SynchronizedRunListenerProxy;

import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;

/**
 * @author Kristian Rosenvold
 */
public class SynchronizedRunlistenerTest {

    @Test
    public void checkCounts()
            throws Exception {
        CountingRunListener countingRunListener = new CountingRunListener();
        SynchronizedRunListenerProxy listenerProxy = new SynchronizedRunListenerProxy(countingRunListener);
        listenerProxy.testRunStarted(Description.createSuiteDescription(this.getClass()));
        assertEquals(1, countingRunListener.runstarted.get());
        listenerProxy.testStarted(Description.createSuiteDescription(this.getClass()));
        assertEquals(1, countingRunListener.started.get());
        listenerProxy.testFinished(Description.createSuiteDescription(this.getClass()));
        assertEquals(1, countingRunListener.finished.get());
        listenerProxy.testFailure(new Failure(Description.createSuiteDescription(this.getClass()), new RuntimeException("foo")));
        assertEquals(1, countingRunListener.failure.get());
        listenerProxy.testAssumptionFailure(new Failure(Description.createSuiteDescription(this.getClass()), new RuntimeException("foo")));
        assertEquals(1, countingRunListener.assumptionFailure.get());
        listenerProxy.testIgnored(Description.createSuiteDescription(this.getClass()));
        assertEquals(1, countingRunListener.ignored.get());


    }

    class CountingRunListener extends RunListener {
        private final AtomicInteger runstarted = new AtomicInteger();
        private final AtomicInteger runfinished = new AtomicInteger();
        private final AtomicInteger started = new AtomicInteger();
        private final AtomicInteger finished = new AtomicInteger();
        private final AtomicInteger failure = new AtomicInteger();
        private final AtomicInteger assumptionFailure = new AtomicInteger();
        private final AtomicInteger ignored = new AtomicInteger();

        @Override
        public void testRunStarted(Description description)
                throws Exception {
            runstarted.incrementAndGet();
        }

        @Override
        public void testRunFinished(Result result)
                throws Exception {
            runfinished.incrementAndGet();
        }

        @Override
        public void testStarted(Description description)
                throws Exception {
            started.incrementAndGet();
        }

        @Override
        public void testFinished(Description description)
                throws Exception {
            finished.incrementAndGet();
        }

        @Override
        public void testFailure(Failure failure)
                throws Exception {
            this.failure.incrementAndGet();
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            this.assumptionFailure.incrementAndGet();
        }

        @Override
        public void testIgnored(Description description)
                throws Exception {
            this.ignored.incrementAndGet();
        }
    }
}
