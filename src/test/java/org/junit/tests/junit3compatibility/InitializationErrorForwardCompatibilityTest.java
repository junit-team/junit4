package org.junit.tests.junit3compatibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junit.framework.TestListener;
import junit.framework.TestResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

public class InitializationErrorForwardCompatibilityTest {
    public static class CantInitialize extends Runner {
        private static final String UNIQUE_ERROR_MESSAGE = "Unique error message";

        public CantInitialize(Class<?> klass) throws Exception {
            throw new Exception(UNIQUE_ERROR_MESSAGE);
        }

        @Override
        public Description getDescription() {
            return Description.EMPTY;
        }

        @Override
        public void run(RunNotifier notifier) {
        }
    }

    @RunWith(CantInitialize.class)
    public static class CantInitializeTests {
    }

    private JUnit4TestAdapter fAdapter;

    @Before
    public void createAdapter() {
        fAdapter = new JUnit4TestAdapter(
                CantInitializeTests.class);
    }

    @Test
    public void initializationErrorsShowUpAsWarnings() {
        assertEquals(1, fAdapter.getTests().size());
    }

    @Test
    public void initializationErrorsAreThrownAtRuntime() {
        TestResult result = new TestResult();
        fAdapter.run(result);
        assertEquals(1, result.errorCount());
        assertEquals(CantInitialize.UNIQUE_ERROR_MESSAGE, result.errors()
                .nextElement().exceptionMessage());
    }

    private final class ErrorRememberingListener implements TestListener {
        private junit.framework.Test fError;

        public void addError(junit.framework.Test test, Throwable e) {
            fError = test;
        }

        public void addFailure(junit.framework.Test test,
                AssertionFailedError t) {
        }

        public void endTest(junit.framework.Test test) {
        }

        public void startTest(junit.framework.Test test) {
        }

        public junit.framework.Test getError() {
            return fError;
        }
    }

    @Test
    public void generatedErrorTestsMatchUp() {
        junit.framework.Test shouldFail = fAdapter.getTests().get(0);
        TestResult result = new TestResult();
        ErrorRememberingListener listener = new ErrorRememberingListener();
        result.addListener(listener);
        fAdapter.run(result);
        assertNotNull(listener.getError());
        assertTrue(shouldFail == listener.getError());
    }

    public static class InitializesWithError extends BlockJUnit4ClassRunner {
        public InitializesWithError(Class<?> klass) throws Exception {
            super(klass);
            throw new Exception();
        }
    }
}
