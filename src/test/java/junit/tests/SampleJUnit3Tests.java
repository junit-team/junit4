package junit.tests;

import junit.framework.TestCase;

/**
 * Container for sample JUnit3-style tests used in integration tests.
 */
public class SampleJUnit3Tests {

    public static class TestWithOneThrowingTestMethod extends TestCase {

        public void testAlwaysThrows() {
            new FakeClassUnderTest().throwsExceptionWithoutCause();
        }
    }

    public static class TestWithThrowingSetUpMethod extends TestCase {

        @Override
        protected void setUp() throws Exception {
            super.setUp();
            new FakeClassUnderTest().throwsExceptionWithoutCause();
        }

        public void testAlwaysPasses() {
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
