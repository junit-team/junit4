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
    public static enum TestStatus {SUCCEEDED, FAILED, SKIPPED }

    public static abstract class AbstractStopwatchTest {
        public static long timeSpent;
        public static long timeSpentIfFinished;
        public static String testName;
        public static String testNameIfFinished;
        public static TestStatus status;

        @Rule
        public final Stopwatch stopwatch = new Stopwatch() {
            @Override
            protected void succeeded(long nanos, Description description) {
                timeSpent = nanos;
                status = TestStatus.SUCCEEDED;
                testName = description.getMethodName();
            }

            @Override
            protected void failed(long nanos, Throwable e, Description description) {
                timeSpent = nanos;
                status = TestStatus.FAILED;
                testName = description.getMethodName();
            }

            @Override
            protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
                timeSpent = nanos;
                status = TestStatus.SKIPPED;
                testName = description.getMethodName();
            }

            @Override
            protected void finished(long nanos, Description description) {
                timeSpentIfFinished = nanos;
                testNameIfFinished = description.getMethodName();
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
        AbstractStopwatchTest.testName = null;
        AbstractStopwatchTest.testNameIfFinished = null;
        AbstractStopwatchTest.status = null;
        AbstractStopwatchTest.timeSpent = 0;
        AbstractStopwatchTest.timeSpentIfFinished = 0;
    }

    @Test
    public void succeeded() {
        Result result = JUnitCore.runClasses(SuccessfulTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(AbstractStopwatchTest.testName, is("successfulTest"));
        assertThat(AbstractStopwatchTest.testName, is(AbstractStopwatchTest.testNameIfFinished));
        assertThat(AbstractStopwatchTest.status, is(TestStatus.SUCCEEDED));
        assertTrue("timeSpent > 0", AbstractStopwatchTest.timeSpent > 0);
        assertThat(AbstractStopwatchTest.timeSpent, is(AbstractStopwatchTest.timeSpentIfFinished));
    }

    @Test
    public void failed() {
        Result result = JUnitCore.runClasses(FailedTest.class);
        assertEquals(1, result.getFailureCount());
        assertThat(AbstractStopwatchTest.testName, is("failedTest"));
        assertThat(AbstractStopwatchTest.testName, is(AbstractStopwatchTest.testNameIfFinished));
        assertThat(AbstractStopwatchTest.status, is(TestStatus.FAILED));
        assertTrue("timeSpent > 0", AbstractStopwatchTest.timeSpent > 0);
        assertThat(AbstractStopwatchTest.timeSpent, is(AbstractStopwatchTest.timeSpentIfFinished));
    }

    @Test
    public void skipped() {
        Result result = JUnitCore.runClasses(SkippedTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(AbstractStopwatchTest.testName, is("skippedTest"));
        assertThat(AbstractStopwatchTest.testName, is(AbstractStopwatchTest.testNameIfFinished));
        assertThat(AbstractStopwatchTest.status, is(TestStatus.SKIPPED));
        assertTrue("timeSpent > 0", AbstractStopwatchTest.timeSpent > 0);
        assertThat(AbstractStopwatchTest.timeSpent, is(AbstractStopwatchTest.timeSpentIfFinished));
    }
}
