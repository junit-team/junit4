package org.junit.runner;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.categories.ExcludeCategories;
import org.junit.experimental.categories.IncludeCategories;
import org.junit.tests.TestSystem;
import org.junit.testsupport.EventCollector;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.testsupport.EventCollectorMatchers.hasTestFinished;
import static org.junit.testsupport.EventCollectorMatchers.hasTestStarted;

public class FilterOptionIntegrationTest {
    private static final String INCLUDES_DUMMY_CATEGORY_0 = "--filter=" +
            IncludeCategories.class.getName() + "=" + DummyCategory0.class.getName();
    private static final String EXCLUDES_DUMMY_CATEGORY_1 = "--filter=" +
            ExcludeCategories.class.getName() + "=" + DummyCategory1.class.getName();

    private JUnitCore jUnitCore = new JUnitCore();
    private EventCollector testListener = new EventCollector();

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
        assertThat(testListener, wasRun(testClass));
    }

    private void assertWasNotRun(Class<?> testClass) {
        assertThat(testListener, not(wasRun(testClass)));
    }

    private Matcher<EventCollector> wasRun(Class<?> testClass) {
        return allOf(hasTestStarted(testClass), hasTestFinished(testClass));
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
