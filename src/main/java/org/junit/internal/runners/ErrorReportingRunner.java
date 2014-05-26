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
    private final List<Throwable> causes;

    private final Class<?> testClass;

    public ErrorReportingRunner(Class<?> testClass, Throwable cause) {
        if (testClass == null) {
            throw new NullPointerException("Test class cannot be null");
        }
        this.testClass = testClass;
        causes = getCauses(cause);
    }

    @Override
    public Description getDescription() {
        Description description = Description.createSuiteDescription(testClass);
        for (Throwable each : causes) {
            description.addChild(describeCause(each));
        }
        return description;
    }

    @Override
    public void run(RunNotifier notifier) {
        for (Throwable each : causes) {
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
        return Description.createTestDescription(testClass,
                "initializationError");
    }

    private void runCause(Throwable child, RunNotifier notifier) {
        Description description = describeCause(child);
        notifier.fireTestStarted(description);
        notifier.fireTestFailure(new Failure(description, child));
        notifier.fireTestFinished(description);
    }
}
