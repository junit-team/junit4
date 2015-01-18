package org.junit.internal.builders;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class IgnoredClassRunner extends Runner {
    private final Class<?> clazz;

    public IgnoredClassRunner(Class<?> testClass) {
        clazz = testClass;
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.fireTestIgnored(getDescription());
    }

    @Override
    public Description getDescription() {
        return Description.createSuiteDescription(clazz);
    }
}