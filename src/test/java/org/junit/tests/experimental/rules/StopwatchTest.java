package org.junit.tests.experimental.rules;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author tibor17
 * @since 4.12
 */
public class StopwatchTest {
    private static enum TestStatus { SUCCEEDED, FAILED, SKIPPED }
    private static Record record;
    private static Record finishedRecord;

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
            fDuration = duration;
            fStatus = status;
            fName = name;
        }
    }

    public static abstract class AbstractStopwatchTest {

        @Rule
        public final Stopwatch stopwatch = new Stopwatch() {
            @Override
            protected void succeeded(long nanos, Description description) {
                StopwatchTest.record= new Record(nanos, TestStatus.SUCCEEDED, description.getMethodName());
            }

            @Override
            protected void failed(long nanos, Throwable e, Description description) {
                StopwatchTest.record= new Record(nanos, TestStatus.FAILED, description.getMethodName());
            }

            @Override
            protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
                StopwatchTest.record= new Record(nanos, TestStatus.SKIPPED, description.getMethodName());
            }

            @Override
            protected void finished(long nanos, Description description) {
                StopwatchTest.finishedRecord= new Record(nanos, description.getMethodName());
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

    @Before
    public void init() {
        record= new Record();
        finishedRecord= new Record();
    }

    @Test
    public void succeeded() {
        Result result = JUnitCore.runClasses(SuccessfulTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(record.fName, is("successfulTest"));
        assertThat(record.fName, is(finishedRecord.fName));
        assertThat(record.fStatus, is(TestStatus.SUCCEEDED));
        assertTrue("timeSpent > 0", record.fDuration > 0);
        assertThat(record.fDuration, is(finishedRecord.fDuration));
    }

    @Test
    public void failed() {
        Result result = JUnitCore.runClasses(FailedTest.class);
        assertEquals(1, result.getFailureCount());
        assertThat(record.fName, is("failedTest"));
        assertThat(record.fName, is(finishedRecord.fName));
        assertThat(record.fStatus, is(TestStatus.FAILED));
        assertTrue("timeSpent > 0", record.fDuration > 0);
        assertThat(record.fDuration, is(finishedRecord.fDuration));
    }

    @Test
    public void skipped() {
        Result result = JUnitCore.runClasses(SkippedTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(record.fName, is("skippedTest"));
        assertThat(record.fName, is(finishedRecord.fName));
        assertThat(record.fStatus, is(TestStatus.SKIPPED));
        assertTrue("timeSpent > 0", record.fDuration > 0);
        assertThat(record.fDuration, is(finishedRecord.fDuration));
    }
}
