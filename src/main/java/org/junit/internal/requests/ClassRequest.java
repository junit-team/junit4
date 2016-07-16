package org.junit.internal.requests;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class ClassRequest extends Request {
    private final Lock runnerLock = new ReentrantLock();

    /*
     * We have to use the f prefix, because IntelliJ's JUnit4IdeaTestRunner uses
     * reflection to access this field. See
     * https://github.com/junit-team/junit4/issues/960
     */
    private final Class<?> fTestClass;
    private final boolean canUseSuiteMethod;
    private volatile Runner runner;

    public ClassRequest(Class<?> testClass, boolean canUseSuiteMethod) {
        this.fTestClass = testClass;
        this.canUseSuiteMethod = canUseSuiteMethod;
    }

    public ClassRequest(Class<?> testClass) {
        this(testClass, true);
    }

    @Override
    public Runner getRunner() {
        if (runner == null) {
            runnerLock.lock();
            try {
                if (runner == null) {
                    runner = new AllDefaultPossibilitiesBuilder(canUseSuiteMethod).safeRunnerForClass(fTestClass);
                }
            } finally {
                runnerLock.unlock();
            }
        }
        return runner;
    }
}