package org.junit.runner;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.internal.RealSystem;
import org.junit.runner.notification.RunListener;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FilterIntegrationTest {
    private static final String INCLUDES_DUMMY_CATEGORY_0 = "--filter=" +
            Categories.CategoryFilter.IncludesAny.class.getName() + "=" + DummyCategory0.class.getName();
    private static final String EXCLUDES_DUMMY_CATEGORY_1 = "--filter=" +
            Categories.CategoryFilter.ExcludesAny.class.getName() + "=" + DummyCategory1.class.getName();

    private JUnitCore jUnitCore;
    private TestListener testListener;

    @Before
    public void setUp() {
        jUnitCore = new JUnitCore();
        testListener = new TestListener();

        jUnitCore.addListener(testListener);
    }

    @Test
    public void shouldRunAllTests() {
        final Result result = runJUnit(
                IgnoredTestClass.class.getName(),
                IgnoredTestMethod.class.getName(),
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertIgnored(IgnoredTestClass.class);
        assertIgnored(IgnoredTestMethod.class);
        assertFinished(DummyTestClass.class);
        assertFinished(DummyTestClass0.class);
        assertFinished(DummyTestClass1.class);
        assertFinished(DummyTestClass01.class);
        assertFinished(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(5));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
        assertThat("ignoreCount does not match", result.getIgnoreCount(), is(2));
    }

    @Test
    public void shouldExcludeSomeTests() {
        final Result result = runJUnit(
                EXCLUDES_DUMMY_CATEGORY_1,
                IgnoredTestClass.class.getName(),
                IgnoredTestMethod.class.getName(),
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertIgnored(IgnoredTestClass.class);
        assertIgnored(IgnoredTestMethod.class);
        assertFinished(DummyTestClass.class);
        assertFinished(DummyTestClass0.class);
        assertIgnored(DummyTestClass1.class);
        assertIgnored(DummyTestClass01.class);
        assertIgnored(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(2));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
        assertThat("ignoreCount does not match", result.getIgnoreCount(), is(5));
    }

    @Test
    public void shouldIncludeSomeTests() {
        final Result result = runJUnit(
                INCLUDES_DUMMY_CATEGORY_0,
                IgnoredTestClass.class.getName(),
                IgnoredTestMethod.class.getName(),
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertIgnored(IgnoredTestClass.class);
        assertIgnored(IgnoredTestMethod.class);
        assertIgnored(DummyTestClass.class);
        assertFinished(DummyTestClass0.class);
        assertIgnored(DummyTestClass1.class);
        assertFinished(DummyTestClass01.class);
        assertFinished(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(3));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
        assertThat("ignoreCount does not match", result.getIgnoreCount(), is(4));
    }

    @Test
    public void shouldCombineFilters() {
        final Result result = runJUnit(
                INCLUDES_DUMMY_CATEGORY_0,
                EXCLUDES_DUMMY_CATEGORY_1,
                IgnoredTestClass.class.getName(),
                IgnoredTestMethod.class.getName(),
                DummyTestClass.class.getName(),
                DummyTestClass0.class.getName(),
                DummyTestClass1.class.getName(),
                DummyTestClass01.class.getName(),
                DummyTestClass0TestMethod1.class.getName());

        assertIgnored(IgnoredTestClass.class);
        assertIgnored(IgnoredTestMethod.class);
        assertIgnored(DummyTestClass.class);
        assertFinished(DummyTestClass0.class);
        assertIgnored(DummyTestClass1.class);
        assertIgnored(DummyTestClass01.class);
        assertIgnored(DummyTestClass0TestMethod1.class);
        assertThat("runCount does not match", result.getRunCount(), is(1));
        assertThat("failureCount does not match", result.getFailureCount(), is(0));
        assertThat("ignoreCount does not match", result.getIgnoreCount(), is(6));
    }

    private Result runJUnit(final String... args) {
        return jUnitCore.runMain(new RealSystem(), args);
    }

    private void assertFinished(Class<?> testClass) {
        assertTrue(testClass.getName() + " expected to finish but did not", testListener.testFinished(testClass));
    }

    private void assertIgnored(Class<?> testClass) {
        assertTrue(testClass.getName() + " expected to be ignored but was not", testListener.testIgnored(testClass));
    }

    private static class TestListener extends RunListener {
        private Set<String> finishedTests = new HashSet<String>();
        private Set<String> ignoredTests = new HashSet<String>();

        @Override
        public void testFinished(final Description description) {
            finishedTests.add(description.getClassName());
        }

        public boolean testFinished(final Class<?> testClass) {
            return finishedTests.contains(testClass.getName());
        }

        @Override
        public void testIgnored(final Description description) {
            ignoredTests.add(description.getClassName());
        }

        public boolean testIgnored(final Class<?> testClass) {
            return ignoredTests.contains(testClass.getName());
        }
    }

    @Ignore
    public static class IgnoredTestClass {
        @Test
        public void dummyTest() {
        }
    }

    public static class IgnoredTestMethod {
        @Ignore
        @Test
        public void dummyTest() {
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

    public static interface DummyCategory0 {}
    public static interface DummyCategory1 {}
}
