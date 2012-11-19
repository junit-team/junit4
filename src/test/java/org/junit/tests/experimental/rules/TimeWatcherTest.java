package org.junit.tests.experimental.rules;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TimeWatcher;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.logging.Logger;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.fail;

/**
 * @author tibor17
 * @since 4.12
 */
public class TimeWatcherTest {
    public static enum TestStatus {PASSED, FAILED, SKIPPED }

    public static abstract class AbstractTimeWatcherTest {
        private static final Logger logger = Logger.getLogger("");

        public static long timeSpent;
        public static String testName;
        public static TestStatus status;

        private static void logInfo() {
            logger.info(String.format("Test '%s' %s, spent %d microseconds",
                    testName, status, TimeWatcher.toMicros(timeSpent)));
        }

        @Rule
        public final TimeWatcher timeWatcher = new TimeWatcher() {
            @Override
            protected void succeeded(long nanos, Description description) {
                timeSpent = nanos;
                status = TestStatus.PASSED;
                testName = description.getMethodName();
                //logInfo();
            }

            @Override
            protected void failed(long nanos, Throwable e, Description description) {
                timeSpent = nanos;
                status = TestStatus.FAILED;
                testName = description.getMethodName();
                //logInfo();
            }

            @Override
            protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
                timeSpent = nanos;
                status = TestStatus.SKIPPED;
                testName = description.getMethodName();
                //logInfo();
            }
        };
    }

    public static class SuccessfulTest extends AbstractTimeWatcherTest {
        @Test
        public void successfulTest() {
        }
    }

    public static class FailedTest extends AbstractTimeWatcherTest {
        @Test
        public void failedTest() {
            fail();
        }
    }

    public static class SkippedTest extends AbstractTimeWatcherTest {
        @Test
        public void skippedTest() {
            assumeTrue(false);
        }
    }

    @Before
    public void init() {
        AbstractTimeWatcherTest.testName = null;
        AbstractTimeWatcherTest.status = null;
        AbstractTimeWatcherTest.timeSpent = 0;
    }

    @Test
    public void succeeded() {
        Result result = JUnitCore.runClasses(SuccessfulTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(AbstractTimeWatcherTest.testName, is(equalTo("successfulTest")));
        assertThat(AbstractTimeWatcherTest.status, is(equalTo(TestStatus.PASSED)));
        assertThat(AbstractTimeWatcherTest.timeSpent, is(not(0L)));
    }

    @Test
    public void failed() {
        Result result = JUnitCore.runClasses(FailedTest.class);
        assertEquals(1, result.getFailureCount());
        assertThat(AbstractTimeWatcherTest.testName, is(equalTo("failedTest")));
        assertThat(AbstractTimeWatcherTest.status, is(equalTo(TestStatus.FAILED)));
        assertThat(AbstractTimeWatcherTest.timeSpent, is(not(0L)));
    }

    @Test
    public void skipped() {
        Result result = JUnitCore.runClasses(SkippedTest.class);
        assertEquals(0, result.getFailureCount());
        assertThat(AbstractTimeWatcherTest.testName, is(equalTo("skippedTest")));
        assertThat(AbstractTimeWatcherTest.status, is(equalTo(TestStatus.SKIPPED)));
        assertThat(AbstractTimeWatcherTest.timeSpent, is(not(0L)));
    }
}
