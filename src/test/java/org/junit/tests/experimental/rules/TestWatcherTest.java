package org.junit.tests.experimental.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.runner.JUnitCore.runClasses;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestWatcherTest {
    public static class ViolatedAssumptionTest {
        private static StringBuilder watchedLog = new StringBuilder();

        @Rule
        public TestRule watcher = new LoggingTestWatcher(watchedLog);

        @Test
        public void succeeds() {
            assumeTrue(false);
        }
    }

    @Test
    public void neitherLogSuccessNorFailedForViolatedAssumption() {
        ViolatedAssumptionTest.watchedLog = new StringBuilder();
        runClasses(ViolatedAssumptionTest.class);
        assertThat(ViolatedAssumptionTest.watchedLog.toString(),
                is("starting skipped finished "));
    }

    public static class InternalViolatedAssumptionTest {
        private static StringBuilder watchedLog = new StringBuilder();

        @Rule
        public TestRule watcher = new TestWatcher() {
            @Override
            protected void starting(Description description) {
                watchedLog.append("starting ");
            }

            @Override
            protected void finished(Description description) {
                watchedLog.append("finished ");
            }

            @Override
            protected void skipped(AssumptionViolatedException e, Description description) {
                watchedLog.append("skipped ");
            }
        };

        @SuppressWarnings("deprecation")
        @Test
        public void succeeds() {
            throw new AssumptionViolatedException("don't run");
        }
    }

    @Test
    public void internalViolatedAssumption() {
        InternalViolatedAssumptionTest.watchedLog = new StringBuilder();
        runClasses(InternalViolatedAssumptionTest.class);
        assertThat(InternalViolatedAssumptionTest.watchedLog.toString(),
                is("starting skipped finished "));
    }

    public static class TestWatcherSkippedThrowsExceptionTest {
        @Rule
        public TestRule watcher = new TestWatcher() {
            @Override
            protected void skipped(AssumptionViolatedException e, Description description) {
                throw new RuntimeException("watcher failure");
            }
        };

        @SuppressWarnings("deprecation")
        @Test
        public void fails() {
            throw new AssumptionViolatedException("test failure");
        }
    }

    @Test
    public void testWatcherSkippedThrowsException() {
        PrintableResult result = testResult(TestWatcherSkippedThrowsExceptionTest.class);
        assertThat(result, failureCountIs(2));
        assertThat(result, hasFailureContaining("test failure"));
        assertThat(result, hasFailureContaining("watcher failure"));
    }

    public static class FailingTest {
        private static StringBuilder watchedLog = new StringBuilder();

        @Rule
        public TestRule watcher = new LoggingTestWatcher(watchedLog);

        @Test
        public void succeeds() {
            fail();
        }
    }

    @Test
    public void logFailingTest() {
        FailingTest.watchedLog = new StringBuilder();
        runClasses(FailingTest.class);
        assertThat(FailingTest.watchedLog.toString(),
                is("starting failed finished "));
    }

    public static class TestWatcherFailedThrowsExceptionTest {
        @Rule
        public TestRule watcher = new TestWatcher() {
            @Override
            protected void failed(Throwable e, Description description) {
                throw new RuntimeException("watcher failure");
            }
        };

        @Test
        public void fails() {
            throw new IllegalArgumentException("test failure");
        }
    }

    @Test
    public void testWatcherFailedThrowsException() {
        PrintableResult result = testResult(TestWatcherFailedThrowsExceptionTest.class);
        assertThat(result, failureCountIs(2));
        assertThat(result, hasFailureContaining("test failure"));
        assertThat(result, hasFailureContaining("watcher failure"));
    }

    public static class TestWatcherStartingThrowsExceptionTest {
        @Rule
        public TestRule watcher = new TestWatcher() {
            @Override
            protected void starting(Description description) {
                throw new RuntimeException("watcher failure");
            }
        };

        @Test
        public void fails() {
            throw new IllegalArgumentException("test failure");
        }
    }

    @Test
    public void testWatcherStartingThrowsException() {
        PrintableResult result = testResult(TestWatcherStartingThrowsExceptionTest.class);
        assertThat(result, failureCountIs(2));
        assertThat(result, hasFailureContaining("test failure"));
        assertThat(result, hasFailureContaining("watcher failure"));
    }

    public static class TestWatcherFailedAndFinishedThrowsExceptionTest {
        @Rule
        public TestRule watcher = new TestWatcher() {
            @Override
            protected void failed(Throwable e, Description description) {
                throw new RuntimeException("watcher failed failure");
            }

            @Override
            protected void finished(Description description) {
                throw new RuntimeException("watcher finished failure");
            }
        };

        @Test
        public void fails() {
            throw new IllegalArgumentException("test failure");
        }
    }

    @Test
    public void testWatcherFailedAndFinishedThrowsException() {
        PrintableResult result = testResult(TestWatcherFailedAndFinishedThrowsExceptionTest.class);
        assertThat(result, failureCountIs(3));
        assertThat(result, hasFailureContaining("test failure"));
        assertThat(result, hasFailureContaining("watcher failed failure"));
        assertThat(result, hasFailureContaining("watcher finished failure"));
    }
}
