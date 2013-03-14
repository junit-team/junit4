package org.junit.runner;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.experimental.categories.IncludeCategories;
import org.junit.runner.notification.RunListener;
import org.junit.tests.TestSystem;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilterOptionIntegrationTest {
    private static final String INCLUDES_DUMMY_CATEGORY_0 = "--filter=" +
            IncludeCategories.class.getName() + "=" + DummyCategory0.class.getName();
    private static final String EXCLUDES_DUMMY_CATEGORY_1 = "--filter=" +
            ExcludeCategories.class.getName() + "=" + DummyCategory1.class.getName();

    private JUnitCore jUnitCore = new JUnitCore();
    private TestListener testListener = new TestListener();

    @Before
    public void setUp() {
        jUnitCore.addListener(testListener);
    }

    @Test
    public void shouldRunAllTests() {
        Result result = runJUnit(
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertWasRun(DummyTestClass.class);
        assertWasRun(DummyTestClass0.class);
        assertWasRun(DummyTestClass1.class);
        assertWasRun(DummyTestClass01.class);
        assertWasRun(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(5));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
    }

    @Test
    public void shouldExcludeSomeTests() {
        Result result = runJUnit(
                EXCLUDES_DUMMY_CATEGORY_1,
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertWasRun(DummyTestClass.class);
        assertWasRun(DummyTestClass0.class);
        assertWasNotRun(DummyTestClass1.class);
        assertWasNotRun(DummyTestClass01.class);
        assertWasNotRun(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(2));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
    }

    @Test
    public void shouldIncludeSomeTests() {
        Result result = runJUnit(
                INCLUDES_DUMMY_CATEGORY_0,
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertWasNotRun(DummyTestClass.class);
        assertWasRun(DummyTestClass0.class);
        assertWasNotRun(DummyTestClass1.class);
        assertWasRun(DummyTestClass01.class);
        assertWasRun(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(3));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
    }

    @Test
    public void shouldCombineFilters() {
        Result result = runJUnit(
                INCLUDES_DUMMY_CATEGORY_0,
                EXCLUDES_DUMMY_CATEGORY_1,
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertWasNotRun(DummyTestClass.class);
        assertWasRun(DummyTestClass0.class);
        assertWasNotRun(DummyTestClass1.class);
        assertWasNotRun(DummyTestClass01.class);
        assertWasNotRun(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(1));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
    }

    private Result runJUnit(final String... args) {
        return jUnitCore.runMain(new TestSystem(), args);
    }

    private void assertWasRun(Class<?> testClass) {
        assertTrue(testClass.getName() + " expected to finish but did not", testListener.wasRun(testClass));
    }

    private void assertWasNotRun(Class<?> testClass) {
        assertFalse(
                testClass.getName() + " expected not to have been started but was",
                testListener.wasRun(testClass));
    }

    private static class TestListener extends RunListener {
        private Set<String> startedTests = new HashSet<String>();
        private Set<String> finishedTests = new HashSet<String>();

        @Override
        public void testFinished(final Description description) {
            finishedTests.add(description.getClassName());
        }

        private boolean testFinished(final Class<?> testClass) {
            return finishedTests.contains(testClass.getName());
        }

        @Override
        public void testStarted(final Description description) {
            startedTests.add(description.getClassName());
        }

        private boolean testStarted(final Class<?> testClass) {
            return startedTests.contains(testClass.getName());
        }

        public boolean wasRun(final Class<?> testClass) {
            return testStarted(testClass) && testFinished(testClass);
        }
    }

    public static class DummyTestClass {
        @Test
        public void dummyTest() {
        }
    }

    @Category(DummyCategory0.class)
    public static class DummyTestClass0 {
        @Test
        public void dummyTest() {
        }
    }

    @Category(DummyCategory1.class)
    public static class DummyTestClass1 {
        @Test
        public void dummyTest() {
        }
    }

    @Category({DummyCategory0.class, DummyCategory1.class})
    public static class DummyTestClass01 {
        @Test
        public void dummyTest() {
        }
    }

    @Category(DummyCategory0.class)
    public static class DummyTestClass0TestMethod1 {
        @Category(DummyCategory1.class)
        @Test
        public void dummyTest() {
        }
    }

    public static interface DummyCategory0 {
    }

    public static interface DummyCategory1 {
    }
}
