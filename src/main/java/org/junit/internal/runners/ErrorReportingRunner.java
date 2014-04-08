package org.junit.internal.runners;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

public class ErrorReportingRunner extends Runner {
    private static final FailureReporter FALIURE_REPORTER = new FailureReporter();
    private static final TestFailureReporter TEST_FALIURE_REPORTER = new TestFailureReporter();
    
    private static class FailureReporter {
        void report(RunNotifier notifier, Description description, Throwable cause) {
            notifier.fireTestFailure(new Failure(description, cause));
        }
    }
    
    private static final class TestFailureReporter extends FailureReporter {
        @Override
        void report(RunNotifier notifier, Description description, Throwable cause) {
            notifier.fireTestStarted(description);
            super.report(notifier, description, cause);
            notifier.fireTestFinished(description);
        }
    }
    
    private final List<Throwable> fCauses;

    private final Class<?> fTestClass;

    public ErrorReportingRunner(Class<?> testClass, Throwable cause) {
        fTestClass = testClass;
        fCauses = getCauses(cause);
    }

    @Override
    public Description getDescription() {
        Description description = Description.createSuiteDescription(fTestClass);
        for (Throwable each : fCauses) {
            description.addChild(describeCause(each));
        }
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        for (Throwable each : fCauses) {
            runCause(each, notifier);
        }
    }

    @SuppressWarnings("deprecation")
    private List<Throwable> getCauses(Throwable cause) {
        if (cause instanceof InvocationTargetException) {
            return getCauses(cause.getCause());
        }
        if (cause instanceof InitializationError) {
            return ((InitializationError) cause).getCauses();
        }
        if (cause instanceof org.junit.internal.runners.InitializationError) {
            return ((org.junit.internal.runners.InitializationError) cause)
                    .getCauses();
        }
        return Arrays.asList(cause);
    }

    private Description describeCause(Throwable child) {
        return Description.createTestDescription(fTestClass,
                "initializationError");
    }

    private void runCause(Throwable child, RunNotifier notifier) {
        boolean errorInTest = !(child instanceof InitializationError);
        FailureReporter failureReporter = (errorInTest) ? TEST_FALIURE_REPORTER : FALIURE_REPORTER; 
        failureReporter.report(notifier, describeCause(child), child);
    }
}
