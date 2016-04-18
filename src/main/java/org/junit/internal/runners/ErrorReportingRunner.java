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
    private final String classNames;
    private final Description description;

    public ErrorReportingRunner(Class<?> testClass, Throwable cause) {
        this(cause, new Class<?>[] { testClass });
    }
    
    public ErrorReportingRunner(Throwable cause, Class<?>... testClasses) {
        if (testClasses == null || testClasses.length == 0) {
            throw new NullPointerException("Test classes cannot be null or empty");
        }
        for (Class<?> testClass : testClasses) {
            if (testClass == null) {
                throw new NullPointerException("Test class cannot be null");
            }
        }
        classNames = getClassNames(testClasses);
        causes = getCauses(cause);
        description = createDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        for (Throwable each : causes) {
            runCause(each, notifier);
        }
    }

    @Override
    public Description getDescription() {
        return description;
    }

    private String getClassNames(Class<?>... testClasses) {
        final StringBuilder builder = new StringBuilder();
        for (Class<?> testClass : testClasses) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(testClass.getName());
        }
        return builder.toString();
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

    private Description createDescription() {
        Description description = Description.createSuiteDescription(classNames);
        for (Throwable each : causes) {
            description.addChild(describeCause());
        }
        return description;
    }

    private void runCause(Throwable child, RunNotifier notifier) {
        Description description = describeCause();
        notifier.fireTestStarted(description);
        notifier.fireTestFailure(new Failure(description, child));
        notifier.fireTestFinished(description);
    }

    private Description describeCause() {
        return Description.createTestDescription(classNames, "initializationError");
    }
}
