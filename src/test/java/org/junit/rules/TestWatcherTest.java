package org.junit.rules;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.rules.ExpectedException.none;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.runners.Enclosed;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.Statement;

import java.util.List;

@RunWith(Enclosed.class)
public class TestWatcherTest {

    @RunWith(Parameterized.class)
    public static class Callbacks {

        @Parameters(name = "{0}")
        public static Object[][] parameters() {
            return new Object[][] {
                    {
                        FailingTest.class,
                        "starting failed finished ",
                        asList("starting failed", "test failed", "failed failed", "finished failed") },
                    {
                        InternalViolatedAssumptionTest.class,
                        "starting deprecated skipped finished ",
                        asList("starting failed", "don't run", "deprecated skipped failed", "finished failed") },
                    {
                        SuccessfulTest.class,
                        "starting succeeded finished ",
                        asList("starting failed", "succeeded failed", "finished failed") },
                    {
                        ViolatedAssumptionTest.class,
                        "starting skipped finished ",
                        asList("starting failed", "Test could not be skipped due to other failures", "skipped failed", "finished failed") }
            };
        }

        @Parameter(0)
        public Class<?> testClass;

        @Parameter(1)
        public String expectedCallbacks;

        @Parameter(2)
        public List<String> expectedFailures;

        private static TestRule selectedRule; //for injecting rule into test classes

        @Test
        public void correctCallbacksCalled() {
            StringBuilder log = new StringBuilder();
            selectedRule = new LoggingTestWatcher(log);
            JUnitCore.runClasses(testClass);
            assertEquals(expectedCallbacks, log.toString());
        }

        @Test
        public void resultHasAllFailuresThrownByCallbacks() {
            selectedRule = new ErroneousTestWatcher();
            PrintableResult result = testResult(testClass);
            assertThat(result, failureCountIs(expectedFailures.size()));
            for (String expectedFailure: expectedFailures) {
                assertThat(result, hasFailureContaining(expectedFailure));
            }
        }

        @Test
        public void testWatcherDoesNotModifyResult() {
            selectedRule = new NoOpRule();
            Result resultNoOpRule = JUnitCore.runClasses(testClass);
            selectedRule = new LoggingTestWatcher(new StringBuilder());
            Result resultTestWatcher = JUnitCore.runClasses(testClass);
            assertEquals(
                    "was successful",
                    resultNoOpRule.wasSuccessful(),
                    resultTestWatcher.wasSuccessful());
            assertEquals(
                    "failure count",
                    resultNoOpRule.getFailureCount(),
                    resultTestWatcher.getFailureCount());
            assertEquals(
                    "ignore count",
                    resultNoOpRule.getIgnoreCount(),
                    resultTestWatcher.getIgnoreCount());
            assertEquals(
                    "run count",
                    resultNoOpRule.getRunCount(),
                    resultTestWatcher.getRunCount());
        }

        private static class NoOpRule implements TestRule {
            public Statement apply(Statement base, Description description) {
                return base;
            }
        }

        private static class ErroneousTestWatcher extends TestWatcher {
            @Override
            protected void succeeded(Description description) {
                throw new RuntimeException("succeeded failed");
            }

            @Override
            protected void failed(Throwable e, Description description) {
                throw new RuntimeException("failed failed");
            }

            @Override
            protected void skipped(org.junit.AssumptionViolatedException e, Description description) {
                throw new RuntimeException("skipped failed");
            }

            @Override
            @SuppressWarnings("deprecation")
            protected void skipped(AssumptionViolatedException e, Description description) {
                throw new RuntimeException("deprecated skipped failed");
            }

            @Override
            protected void starting(Description description) {
                throw new RuntimeException("starting failed");
            }

            @Override
            protected void finished(Description description) {
                throw new RuntimeException("finished failed");
            }
        }

        public static class FailingTest {
            @Rule
            public TestRule rule = selectedRule;

            @Test
            public void test() {
                fail("test failed");
            }
        }

        public static class InternalViolatedAssumptionTest {
            @Rule
            public TestRule watcher = selectedRule;

            @SuppressWarnings("deprecation")
            @Test
            public void test() {
                throw new AssumptionViolatedException("don't run");
            }
        }

        public static class SuccessfulTest {
            @Rule
            public TestRule watcher = selectedRule;

            @Test
            public void test() {
            }
        }

        public static class ViolatedAssumptionTest {
            @Rule
            public TestRule watcher = selectedRule;

            @Test
            public void test() {
                assumeTrue(false);
            }
        }
    }

    public static class CallbackArguments {

        public static class Succeeded {
            private static Description catchedDescription;

            @Rule
            public final TestRule watcher = new TestWatcher() {
                @Override
                protected void succeeded(Description description) {
                    catchedDescription = description;
                }
            };

            @Test
            public void test() {
            }
        }

        @Test
        public void succeeded() {
            JUnitCore.runClasses(Succeeded.class);
            assertEquals("test(org.junit.rules.TestWatcherTest$CallbackArguments$Succeeded)",
                    Succeeded.catchedDescription.getDisplayName());
        }

        public static class Failed {
            private static Description catchedDescription;
            private static Throwable catchedThrowable;

            @Rule
            public final TestRule watcher = new TestWatcher() {
                @Override
                protected void failed(Throwable e, Description description) {
                    catchedDescription = description;
                    catchedThrowable = e;
                }
            };

            @Test
            public void test() {
                fail("test failed");
            }
        }

        @Test
        public void failed() {
            JUnitCore.runClasses(Failed.class);
            assertEquals("test failed", Failed.catchedThrowable.getMessage());
            assertEquals(AssertionError.class, Failed.catchedThrowable.getClass());
            assertEquals("test(org.junit.rules.TestWatcherTest$CallbackArguments$Failed)",
                    Failed.catchedDescription.getDisplayName());
        }

        public static class Skipped {
            private static Description catchedDescription;
            private static org.junit.AssumptionViolatedException catchedException;

            @Rule
            public final TestRule watcher = new TestWatcher() {
                @Override
                protected void skipped(org.junit.AssumptionViolatedException e, Description description) {
                    catchedDescription = description;
                    catchedException = e;
                }
            };

            @Test
            public void test() {
                assumeTrue("test skipped", false);
            }
        }

        @Test
        public void skipped() {
            JUnitCore.runClasses(Skipped.class);
            assertEquals("test skipped", Skipped.catchedException.getMessage());
            assertEquals(org.junit.AssumptionViolatedException.class, Skipped.catchedException.getClass());
            assertEquals("test(org.junit.rules.TestWatcherTest$CallbackArguments$Skipped)",
                    Skipped.catchedDescription.getDisplayName());
        }

        public static class DeprecatedSkipped {
            private static Description catchedDescription;
            private static AssumptionViolatedException catchedException;

            @Rule
            public final TestRule watcher = new TestWatcher() {
                @Override
                @SuppressWarnings("deprecation")
                protected void skipped(AssumptionViolatedException e, Description description) {
                    catchedDescription = description;
                    catchedException = e;
                }
            };

            @SuppressWarnings("deprecation")
            @Test
            public void test() {
                throw new AssumptionViolatedException("test skipped");
            }
        }

        @Test
        public void deprecatedSkipped() {
            JUnitCore.runClasses(DeprecatedSkipped.class);
            assertEquals("test skipped", DeprecatedSkipped.catchedException.getMessage());
            assertEquals(AssumptionViolatedException.class, DeprecatedSkipped.catchedException.getClass());
            assertEquals("test(org.junit.rules.TestWatcherTest$CallbackArguments$DeprecatedSkipped)",
                    DeprecatedSkipped.catchedDescription.getDisplayName());
        }

        public static class Starting {
            private static Description catchedDescription;

            @Rule
            public final TestRule watcher = new TestWatcher() {
                @Override
                protected void starting(Description description) {
                    catchedDescription = description;
                }
            };

            @Test
            public void test() {
            }
        }

        @Test
        public void starting() {
            JUnitCore.runClasses(Starting.class);
            assertEquals("test(org.junit.rules.TestWatcherTest$CallbackArguments$Starting)",
                    Starting.catchedDescription.getDisplayName());
        }

        public static class Finished {
            private static Description catchedDescription;

            @Rule
            public final TestRule watcher = new TestWatcher() {
                @Override
                protected void finished(Description description) {
                    catchedDescription = description;
                }
            };

            @Test
            public void test() {
            }
        }

        @Test
        public void finished() {
            JUnitCore.runClasses(Finished.class);
            assertEquals("test(org.junit.rules.TestWatcherTest$CallbackArguments$Finished)",
                    Finished.catchedDescription.getDisplayName());
        }
    }

    //The following tests check the information in TestWatcher's Javadoc
    //regarding interplay with other rules.
    public static class InterplayWithOtherRules {
        private static StringBuilder log;

        public static class ExpectedExceptionTest {
            @Rule(order = Integer.MIN_VALUE)
            //the field name must be alphabetically lower than "thrown" in order
            //to make the test failing if order is not set
            public final TestRule a = new LoggingTestWatcher(log);

            @Rule
            public final ExpectedException thrown = none();

            @Test
            public void testWithExpectedException() {
                thrown.expect(RuntimeException.class);
                throw new RuntimeException("expected exception");
            }
        }

        @Test
        public void expectedExceptionIsSeenAsSuccessfulTest() {
            log = new StringBuilder();
            JUnitCore.runClasses(ExpectedExceptionTest.class);
            assertEquals("starting succeeded finished ", log.toString());
        }

        public static class ErrorCollectorTest {
            @Rule(order = Integer.MIN_VALUE)
            //the field name must be alphabetically lower than "collector" in
            //order to make the test failing if order is not set
            public final TestRule a = new LoggingTestWatcher(log);

            @Rule
            public final ErrorCollector collector = new ErrorCollector();

            @Test
            public void test() {
                collector.addError(new RuntimeException("expected exception"));
            }
        }

        @Test
        public void testIsSeenAsFailedBecauseOfCollectedError() {
            log = new StringBuilder();
            JUnitCore.runClasses(ErrorCollectorTest.class);
            assertEquals("starting failed finished ", log.toString());
        }
    }
}
