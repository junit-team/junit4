package org.junit.tests.running.classes;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.rules.RuleMemberValidatorTest.TestWithNonStaticClassRule;
import org.junit.rules.RuleMemberValidatorTest.TestWithProtectedClassRule;

public class ParentRunnerTest {
    public static String log = "";

    public static class FruitTest {
        @Test
        public void apple() {
            log += "apple ";
        }

        @Test
        public void /* must hash-sort after "apple" */Banana() {
            log += "banana ";
        }
    }

    @Test
    public void useChildHarvester() throws InitializationError {
        log = "";
        ParentRunner<?> runner = new BlockJUnit4ClassRunner(FruitTest.class);
        runner.setScheduler(new RunnerScheduler() {
            public void schedule(Runnable childStatement) {
                log += "before ";
                childStatement.run();
                log += "after ";
            }

            public void finished() {
                log += "afterAll ";
            }
        });

        runner.run(new RunNotifier());
        assertEquals("before apple after before banana after afterAll ", log);
    }

    @Test
    public void testMultipleFilters() throws Exception {
        JUnitCore junitCore = new JUnitCore();
        Request request = Request.aClass(ExampleTest.class);
        Request requestFiltered = request.filterWith(new Exclude("test1"));
        Request requestFilteredFiltered = requestFiltered
                .filterWith(new Exclude("test2"));
        Result result = junitCore.run(requestFilteredFiltered);
        assertThat(result.getFailures(), isEmpty());
        assertEquals(1, result.getRunCount());
    }

    private Matcher<List<?>> isEmpty() {
        return new TypeSafeMatcher<List<?>>() {
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("is empty");
            }

            @Override
            public boolean matchesSafely(List<?> item) {
                return item.size() == 0;
            }
        };
    }

    private static class Exclude extends Filter {
        private final String methodName;

        public Exclude(String methodName) {
            this.methodName = methodName;
        }

        @Override
        public boolean shouldRun(Description description) {
            return !description.getMethodName().equals(methodName);
        }

        @Override
        public String describe() {
            return "filter method name: " + methodName;
        }
    }

    public static class ExampleTest {
        @Test
        public void test1() throws Exception {
        }

        @Test
        public void test2() throws Exception {
        }

        @Test
        public void test3() throws Exception {
        }
    }

    @Test
    public void failWithHelpfulMessageForProtectedClassRule() {
        assertClassHasFailureMessage(TestWithProtectedClassRule.class,
                "The @ClassRule 'temporaryFolder' must be public.");
    }

    @Test
    public void failWithHelpfulMessageForNonStaticClassRule() {
        assertClassHasFailureMessage(TestWithNonStaticClassRule.class,
                "The @ClassRule 'temporaryFolder' must be static.");
    }

    static class NonPublicTestClass {
        public NonPublicTestClass() {
        }

        @Test
        public void alwaysPasses() {}
    }

    @Test
    public void cannotBeCreatedWithNonPublicTestClass() {
        assertClassHasFailureMessage(
                NonPublicTestClass.class,
                "The class org.junit.tests.running.classes.ParentRunnerTest$NonPublicTestClass is not public.");
    }

    private void assertClassHasFailureMessage(Class<?> klass, String message) {
        JUnitCore junitCore = new JUnitCore();
        Request request = Request.aClass(klass);
        Result result = junitCore.run(request);
        assertThat(result.getFailureCount(), is(1));
        assertThat(result.getFailures().get(0).getMessage(),
                containsString(message));
    }

    public static class AssertionErrorAtParentLevelTest {
        @BeforeClass
        public static void beforeClass() throws Throwable {
            throw new AssertionError("Thrown from @BeforeClass");
        }

        @Test
        public void test() {}
    }

    @Test
    public void assertionErrorAtParentLevelTest() throws InitializationError {
        CountingRunListener countingRunListener = runTestWithParentRunner(AssertionErrorAtParentLevelTest.class);
        Assert.assertEquals(1, countingRunListener.testSuiteStarted);
        Assert.assertEquals(1, countingRunListener.testSuiteFinished);
        Assert.assertEquals(1, countingRunListener.testSuiteFailure);
        Assert.assertEquals(0, countingRunListener.testSuiteAssumptionFailure);

        Assert.assertEquals(0, countingRunListener.testStarted);
        Assert.assertEquals(0, countingRunListener.testFinished);
        Assert.assertEquals(0, countingRunListener.testFailure);
        Assert.assertEquals(0, countingRunListener.testAssumptionFailure);
        Assert.assertEquals(0, countingRunListener.testIgnored);
    }

    public static class AssumptionViolatedAtParentLevelTest {
        @SuppressWarnings("deprecation")
        @BeforeClass
        public static void beforeClass() {
            throw new AssumptionViolatedException("Thrown from @BeforeClass");
        }

        @Test
        public void test() {}
    }

    @Test
    public void assumptionViolatedAtParentLevel() throws InitializationError {
        CountingRunListener countingRunListener = runTestWithParentRunner(AssumptionViolatedAtParentLevelTest.class);
        Assert.assertEquals(1, countingRunListener.testSuiteStarted);
        Assert.assertEquals(1, countingRunListener.testSuiteFinished);
        Assert.assertEquals(0, countingRunListener.testSuiteFailure);
        Assert.assertEquals(1, countingRunListener.testSuiteAssumptionFailure);

        Assert.assertEquals(0, countingRunListener.testStarted);
        Assert.assertEquals(0, countingRunListener.testFinished);
        Assert.assertEquals(0, countingRunListener.testFailure);
        Assert.assertEquals(0, countingRunListener.testAssumptionFailure);
        Assert.assertEquals(0, countingRunListener.testIgnored);
    }

    public static class TestTest {
        @Test
        public void pass() {}

        @Test
        public void fail() {
            throw new AssertionError("Thrown from @Test");
        }

        @Ignore
        @Test
        public void ignore() {}

        @SuppressWarnings("deprecation")
        @Test
        public void assumptionFail() {
            throw new AssumptionViolatedException("Thrown from @Test");
        }
    }

    @Test
    public void parentRunnerTestMethods() throws InitializationError {
        CountingRunListener countingRunListener = runTestWithParentRunner(TestTest.class);
        Assert.assertEquals(1, countingRunListener.testSuiteStarted);
        Assert.assertEquals(1, countingRunListener.testSuiteFinished);
        Assert.assertEquals(0, countingRunListener.testSuiteFailure);
        Assert.assertEquals(0, countingRunListener.testSuiteAssumptionFailure);

        Assert.assertEquals(3, countingRunListener.testStarted);
        Assert.assertEquals(3, countingRunListener.testFinished);
        Assert.assertEquals(1, countingRunListener.testFailure);
        Assert.assertEquals(1, countingRunListener.testAssumptionFailure);
        Assert.assertEquals(1, countingRunListener.testIgnored);
    }

    private CountingRunListener runTestWithParentRunner(Class<?> testClass) throws InitializationError {
        CountingRunListener listener = new CountingRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(listener);
        ParentRunner<?> runner = new BlockJUnit4ClassRunner(testClass);
        runner.run(runNotifier);
        return listener;
    }

    private static class CountingRunListener extends RunListener {
        private int testSuiteStarted = 0;
        private int testSuiteFinished = 0;
        private int testSuiteFailure = 0;
        private int testSuiteAssumptionFailure = 0;

        private int testStarted = 0;
        private int testFinished = 0;
        private int testFailure = 0;
        private int testAssumptionFailure = 0;
        private int testIgnored = 0;

        @Override
        public void testSuiteStarted(Description description) throws Exception {
            testSuiteStarted++;
        }

        @Override
        public void testSuiteFinished(Description description) throws Exception {
            testSuiteFinished++;
        }

        @Override
        public void testStarted(Description description) throws Exception {
            testStarted++;
        }

        @Override
        public void testFinished(Description description) throws Exception {
            testFinished++;
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            if (failure.getDescription().isSuite()) {
                testSuiteFailure++;
            } else {
                testFailure++;
            }
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            if (failure.getDescription().isSuite()) {
                testSuiteAssumptionFailure++;
            } else {
                testAssumptionFailure++;
            }
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            testIgnored++;
        }
    }
}
