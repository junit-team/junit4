package org.junit.tests.experimental.rules;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotEquals;

/**
 * @author tibor17
 * @since 4.12
 */
public class StopwatchTest {
    private static enum TestStatus { SUCCEEDED, FAILED, SKIPPED }
    private static Record fRecord;
    private static Record fFinishedRecord;

    private static class Record {
        final long fDuration;
        final String fName;
        final TestStatus fStatus;

        Record() {
            this(0, null, null);
        }

        Record(long duration, String name) {
            this(duration, null, name);
        }

        Record(long duration, TestStatus status, String name) {
            fDuration= duration;
            fStatus= status;
            fName= name;
        }
    }

    public static abstract class AbstractStopwatchTest {

        @Rule
        public final Stopwatch fStopwatch= new Stopwatch() {
            @Override
            protected void succeeded(long nanos, Description description) {
                StopwatchTest.fRecord= new Record(nanos, TestStatus.SUCCEEDED, description.getMethodName());
            }

            @Override
            protected void failed(long nanos, Throwable e, Description description) {
                StopwatchTest.fRecord= new Record(nanos, TestStatus.FAILED, description.getMethodName());
            }

            @Override
            protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
                StopwatchTest.fRecord= new Record(nanos, TestStatus.SKIPPED, description.getMethodName());
            }

            @Override
            protected void finished(long nanos, Description description) {
                StopwatchTest.fFinishedRecord= new Record(nanos, description.getMethodName());
            }
        };
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

    public static class WrongDurationTest extends AbstractStopwatchTest {
        @Test
        public void wrongDuration() throws InterruptedException {
            Thread.sleep(500L);
            assertNotEquals(fStopwatch.runtime(MILLISECONDS), 300d, 100d);
        }
    }

    public static class DurationTest extends AbstractStopwatchTest {
        @Test
        public void duration() throws InterruptedException {
            Thread.sleep(300L);
            assertEquals(300d, fStopwatch.runtime(MILLISECONDS), 100d);
            Thread.sleep(500L);
            assertEquals(800d, fStopwatch.runtime(MILLISECONDS), 250d);
        }
    }

    @Before
    public void init() {
        fRecord= new Record();
        fFinishedRecord= new Record();
    }

    @Test
    public void succeeded() {
        Result result= JUnitCore.runClasses(SuccessfulTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(fRecord.fName, is("successfulTest"));
        assertThat(fRecord.fName, is(fFinishedRecord.fName));
        assertThat(fRecord.fStatus, is(TestStatus.SUCCEEDED));
        assertTrue("timeSpent > 0", fRecord.fDuration > 0);
        assertThat(fRecord.fDuration, is(fFinishedRecord.fDuration));
    }

    @Test
    public void failed() {
        Result result= JUnitCore.runClasses(FailedTest.class);
        assertEquals(1, result.getFailureCount());
        assertThat(fRecord.fName, is("failedTest"));
        assertThat(fRecord.fName, is(fFinishedRecord.fName));
        assertThat(fRecord.fStatus, is(TestStatus.FAILED));
        assertTrue("timeSpent > 0", fRecord.fDuration > 0);
        assertThat(fRecord.fDuration, is(fFinishedRecord.fDuration));
    }

    @Test
    public void skipped() {
        Result result= JUnitCore.runClasses(SkippedTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(fRecord.fName, is("skippedTest"));
        assertThat(fRecord.fName, is(fFinishedRecord.fName));
        assertThat(fRecord.fStatus, is(TestStatus.SKIPPED));
        assertTrue("timeSpent > 0", fRecord.fDuration > 0);
        assertThat(fRecord.fDuration, is(fFinishedRecord.fDuration));
    }

    @Test
    public void wrongDuration() {
        Result result= JUnitCore.runClasses(WrongDurationTest.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void duration() {
        Result result= JUnitCore.runClasses(DurationTest.class);
        assertTrue(result.wasSuccessful());
    }
}
