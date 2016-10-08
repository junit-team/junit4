package org.junit.internal.requests;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.builders.SuiteMethodBuilder;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

public class ClassRequest extends Request {
    private final Object runnerLock = new Object();

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
            synchronized (runnerLock) {
                if (runner == null) {
                    runner = new CustomAllDefaultPossibilitiesBuilder().safeRunnerForClass(fTestClass);
                }
            }
        }
        return runner;
    }

    private class CustomAllDefaultPossibilitiesBuilder extends AllDefaultPossibilitiesBuilder {

        @Override
        protected RunnerBuilder suiteMethodBuilder() {
            return new CustomSuiteMethodBuilder();
        }
    }

    /*
     * Customization of {@link SuiteMethodBuilder} that prevents use of the
     * suite method when creating a runner for fTestClass when canUseSuiteMethod
     * is false.
     */
    private class CustomSuiteMethodBuilder extends SuiteMethodBuilder {

        @Override
        public Runner runnerForClass(Class<?> testClass) throws Throwable {
            if (testClass == fTestClass && !canUseSuiteMethod) {
                return null;
            }
            return super.runnerForClass(testClass);
        }
    }
}