package org.junit.runner;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.RunnerBuilder;

public class RunnerSpy extends Runner {
    public static final Description DESCRIPTION = Description.TEST_MECHANISM;

    private RunnerBuilder invokedRunnerBuilder;
    private Class<?> invokedTestClass;

    public RunnerSpy(Class<?> testClass) {
        invokedTestClass = testClass;
    }

    public RunnerSpy(Class<?> testClass, RunnerBuilder runnerBuilder) {
        invokedTestClass = testClass;
        invokedRunnerBuilder = runnerBuilder;
    }

    @Override
    public Description getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void run(RunNotifier runNotifier) {
    }

    public RunnerBuilder getInvokedRunnerBuilder() {
        return invokedRunnerBuilder;
    }

    public Class<?> getInvokedTestClass() {
        return invokedTestClass;
    }
}