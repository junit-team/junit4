package org.junit.runners;

import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.filters.IgnoreFilter;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FilteredClassRunnerTest {
    @Test
    public void filteredOutTestShouldFireTestIgnored() throws Exception {
        final NotifierSpy notifierSpy = new NotifierSpy();

        final Class<DummyTest> dummyTestClass = DummyTest.class;
        final FilteredClassRunner filteredClassRunner =
                new FilteredClassRunner(null, dummyTestClass, new IgnoreFilter());
        final Method dummyTest = dummyTestClass.getMethod("dummyTest");

        filteredClassRunner.runChild(new FrameworkMethod(dummyTest), notifierSpy);

        assertTestIsIgnored(notifierSpy);
    }

    @Test
    public void nonFilteredOutTestShouldRun() throws Exception {
        final NotifierSpy notifierSpy = new NotifierSpy();

        final Class<DummyTest> dummyTestClass = DummyTest.class;
        final FilteredClassRunner filteredClassRunner = new FilteredClassRunner(
                new BlockJUnit4ClassRunner(dummyTestClass),
                dummyTestClass,
                new PassThroughFilter());
        final Method dummyTest = dummyTestClass.getMethod("dummyTest");

        filteredClassRunner.runChild(new FrameworkMethod(dummyTest), notifierSpy);

        assertTestIsRan(notifierSpy);
    }

    private void assertTestIsIgnored(final NotifierSpy notifierSpy) {
        assertTrue(notifierSpy.isFireTestIgnoredCalled());
        assertFalse(notifierSpy.isFireTestStartedCalled());
        assertFalse(notifierSpy.isFireTestFinishedCalled());
    }

    private void assertTestIsRan(final NotifierSpy notifierSpy) {
        assertFalse(notifierSpy.isFireTestIgnoredCalled());
        assertTrue(notifierSpy.isFireTestStartedCalled());
        assertTrue(notifierSpy.isFireTestFinishedCalled());
    }

    private class PassThroughFilter extends Filter {
        @Override
        public boolean shouldRun(final Description description) {
            return true;
        }

        @Override
        public String describe() {
            return "Pass-Through Filter";
        }

        @Override
        public Filter intersect(final Filter second) {
            return second;
        }
    }

    private static class NotifierSpy extends RunNotifier {
        private boolean fireTestIgnoredCalled = false;
        private boolean fireTestStartedCalled = false;
        private boolean fireTestFinishedCalled = false;

        @Override
        public void fireTestIgnored(final Description description) {
            fireTestIgnoredCalled = true;
        }

        public boolean isFireTestIgnoredCalled() {
            return fireTestIgnoredCalled;
        }

        @Override
        public void fireTestStarted(final Description description) {
            fireTestStartedCalled = true;
        }

        public boolean isFireTestStartedCalled() {
            return fireTestStartedCalled;
        }

        @Override
        public void fireTestFinished(final Description description) {
            fireTestFinishedCalled = true;
        }

        public boolean isFireTestFinishedCalled() {
            return fireTestFinishedCalled;
        }
    }

    public static class DummyTest {
        @Ignore
        @Test
        public void dummyTest() {
        }
    }
}
