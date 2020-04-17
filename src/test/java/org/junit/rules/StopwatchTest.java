package org.junit.rules;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.util.concurrent.TimeUnit;

import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

/**
 * @author tibor17
 * @since 4.12
 */
public class StopwatchTest {
    private static enum TestStatus { SUCCEEDED, FAILED, SKIPPED }
    private static Record record;
    private static Record finishedRecord;
    private static long fakeTimeNanos = 1234;

    private static class Record {
        final long duration;
        final String name;
        final TestStatus status;

        Record() {
            this(0, null, null);
        }

        Record(long duration, Description description) {
            this(duration, null, description);
        }

        Record(long duration, TestStatus status, Description description) {
            this.duration = duration;
            this.status = status;
            this.name = description == null ? null : description.getMethodName();
        }
    }

    public static abstract class AbstractStopwatchTest {

        /**
         * Fake implementation of {@link Stopwatch.Clock} that increments the time
         * every time it is asked.
         */
        private final Stopwatch.Clock fakeClock = new Stopwatch.Clock() {
            @Override
            public long nanoTime() {
                return fakeTimeNanos++;
            }
        };

        protected final Stopwatch stopwatch = new Stopwatch(fakeClock) {
            @Override
            protected void succeeded(long nanos, Description description) {
                StopwatchTest.record = new Record(nanos, TestStatus.SUCCEEDED, description);
                simulateTimePassing(1);
            }

            @Override
            protected void failed(long nanos, Throwable e, Description description) {
                StopwatchTest.record = new Record(nanos, TestStatus.FAILED, description);
                simulateTimePassing(1);
            }

            @Override
            protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
                StopwatchTest.record = new Record(nanos, TestStatus.SKIPPED, description);
                simulateTimePassing(1);
            }

            @Override
            protected void finished(long nanos, Description description) {
                StopwatchTest.finishedRecord = new Record(nanos, description);
            }
        };

        private final TestWatcher watcher = new TestWatcher() {
            @Override
            protected void finished(Description description) {
                afterStopwatchRule();
            }
        };

        @Rule
        public final RuleChain chain = RuleChain
            .outerRule(watcher)
            .around(stopwatch);

        protected void afterStopwatchRule() {
        }
    }

    public static class SuccessfulTest extends AbstractStopwatchTest {
        @Test
        public void successfulTest() {
        }
    }

    public static class FailedTest extends AbstractStopwatchTest {
        @Test
        public void failedTest() {
            fail();
        }
    }

    public static class SkippedTest extends AbstractStopwatchTest {
        @Test
        public void skippedTest() {
            assumeTrue(false);
        }
    }

    public static class DurationDuringTestTest extends AbstractStopwatchTest {
        @Test
        public void duration() {
            simulateTimePassing(300L);
            assertEquals(300L, stopwatch.runtime(MILLISECONDS));
            simulateTimePassing(500L);
            assertEquals(800L, stopwatch.runtime(MILLISECONDS));
        }
    }

    public static class DurationAfterTestTest extends AbstractStopwatchTest {
        @Test
        public void duration() {
            simulateTimePassing(300L);
            assertEquals(300L, stopwatch.runtime(MILLISECONDS));
        }

        @Override
        protected void afterStopwatchRule() {
            assertEquals(300L, stopwatch.runtime(MILLISECONDS));
            simulateTimePassing(500L);
            assertEquals(300L, stopwatch.runtime(MILLISECONDS));
        }
    }

    @Before
    public void init() {
        record = new Record();
        finishedRecord = new Record();
        simulateTimePassing(1L);
    }

    private static Result runTest(Class<?> test) {
        simulateTimePassing(1L);
        JUnitCore junitCore = new JUnitCore();
        return junitCore.run(Request.aClass(test).getRunner());
    }

    private static void simulateTimePassing(long millis) {
        fakeTimeNanos += TimeUnit.MILLISECONDS.toNanos(millis);
    }

    @Test
    public void succeeded() {
        Result result = runTest(SuccessfulTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(record.name, is("successfulTest"));
        assertThat(record.name, is(finishedRecord.name));
        assertThat(record.status, is(TestStatus.SUCCEEDED));
        assertTrue("timeSpent > 0", record.duration > 0);
        assertThat(record.duration, is(finishedRecord.duration));
    }

    @Test
    public void failed() {
        Result result = runTest(FailedTest.class);
        assertEquals(1, result.getFailureCount());
        assertThat(record.name, is("failedTest"));
        assertThat(record.name, is(finishedRecord.name));
        assertThat(record.status, is(TestStatus.FAILED));
        assertTrue("timeSpent > 0", record.duration > 0);
        assertThat(record.duration, is(finishedRecord.duration));
    }

    @Test
    public void skipped() {
        Result result = runTest(SkippedTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(record.name, is("skippedTest"));
        assertThat(record.name, is(finishedRecord.name));
        assertThat(record.status, is(TestStatus.SKIPPED));
        assertTrue("timeSpent > 0", record.duration > 0);
        assertThat(record.duration, is(finishedRecord.duration));
    }

    @Test
    public void runtimeDuringTestShouldReturnTimeSinceStart() {
        Result result = runTest(DurationDuringTestTest.class);
        assertTrue(result.wasSuccessful());
    }

  @Test
    public void runtimeAfterTestShouldReturnRunDuration() {
        Result result = runTest(DurationAfterTestTest.class);
        assertTrue(result.wasSuccessful());
    }
}
