package org.junit.internal.requests;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ClassRequest extends Request {
    private final Object runnerLock = new Object();
    private final Class<?> testClass;
    private final boolean canUseSuiteMethod;
    private volatile Runner runner;

    public ClassRequest(Class<?> testClass, boolean canUseSuiteMethod) {
        this.testClass = testClass;
        this.canUseSuiteMethod = canUseSuiteMethod;
    }

    public ClassRequest(Class<?> testClass) {
        this(testClass, true);
    }

    @Override
    public Runner getRunner() {
        if (runner == null) {
            synchronized (runnerLock) {
                if (runner == null) {
                    runner = new AllDefaultPossibilitiesBuilder(canUseSuiteMethod).safeRunnerForClass(testClass);
                }
            }
        }
        return runner;
    }
}