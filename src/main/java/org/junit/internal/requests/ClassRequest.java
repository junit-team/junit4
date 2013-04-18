package org.junit.internal.requests;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ClassRequest extends Request {
    private final Runner fRunner;

    public ClassRequest(Class<?> testClass, boolean canUseSuiteMethod) {
        fRunner = new AllDefaultPossibilitiesBuilder(canUseSuiteMethod).safeRunnerForClass(testClass);
    }

    public ClassRequest(Class<?> testClass) {
        this(testClass, true);
    }

    @Override
    public Runner getRunner() {
        return fRunner;
    }
}