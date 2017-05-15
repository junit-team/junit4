package org.junit.tests;

import org.junit.Test;

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
