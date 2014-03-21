package org.junit.internal.builders;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class IgnoredClassRunner extends Runner {
    private final Class<?> testClass;

    public IgnoredClassRunner(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.fireTestIgnored(getDescription());
    }

    @Override
    public Description getDescription() {
        return Description.createSuiteDescription(testClass);
    }
}