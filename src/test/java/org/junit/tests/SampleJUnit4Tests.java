package org.junit.tests;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;

/**
 * Container for sample JUnit4-style tests used in integration tests.
 */
public class SampleJUnit4Tests {

    public static class TestWithOneThrowingTestMethod {
        
        @Test
        public void alwaysThrows() {
            new FakeClassUnderTest().throwsExceptionWithoutCause();
        }
    }

    public static class TestWithOneThrowingTestMethodWithCause {
        
        @Test
        public void alwaysThrows() {
            new FakeClassUnderTest().throwsExceptionWithCause();
        }
    }

    public static class TestWithThrowingBeforeMethod {

        @Before
        public void alwaysThrows() {
            new FakeClassUnderTest().throwsExceptionWithoutCause();
        }

        @Test
        public void alwaysPasses() {
        }
    }

    public static class ThrowingTestRule implements TestRule {

        public Statement apply(
                Statement base, org.junit.runner.Description description) {
            new FakeClassUnderTest().throwsExceptionWithoutCause();
            return base;
        }
    }

    public static class TestWithThrowingTestRule {

        @Rule
        public final TestRule rule = new ThrowingTestRule();

        @Test
        public void alwaysPasses() {
        }
    }

    public static class TestWithThrowingClassRule {

        @ClassRule
        public static final TestRule rule = new ThrowingTestRule();

        @Test
        public void alwaysPasses() {
        }
    }

    public static class ThrowingMethodRule implements MethodRule {

        public Statement apply(
                Statement base, FrameworkMethod method, Object target) {
            new FakeClassUnderTest().throwsExceptionWithoutCause();
            return base;
        }
    }

    public static class TestWithThrowingMethodRule {

        @Rule
        public final ThrowingMethodRule rule = new ThrowingMethodRule();

        @Test
        public void alwaysPasses() {
        }
    }

    public static class TestWithSuppressedException {
        public static final Method addSuppressed = initAddSuppressed();

        static Method initAddSuppressed() {
            try {
                return Throwable.class.getMethod("addSuppressed", Throwable.class);
            } catch (Throwable e) {
                return null;
            }
        }

        @Test
        public void alwaysThrows() throws Exception {
            final RuntimeException exception = new RuntimeException("error");
            addSuppressed.invoke(exception, new RuntimeException("suppressed"));
            throw exception;
        }
    }

    private static class FakeClassUnderTest {
        
        public void throwsExceptionWithCause() {
            doThrowExceptionWithCause();
        }

        public void throwsExceptionWithoutCause() {
            doThrowExceptionWithoutCause();
        }

        private void doThrowExceptionWithCause() {
            try {
                throwsExceptionWithoutCause();
            } catch (Exception e) {
                throw new RuntimeException("outer", e);
            }
        }

        private void doThrowExceptionWithoutCause() {
            throw new RuntimeException("cause");
        }
    }
}
