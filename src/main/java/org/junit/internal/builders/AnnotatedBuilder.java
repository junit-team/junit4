package org.junit.internal.builders;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.lang.reflect.Constructor;

public class AnnotatedBuilder extends RunnerBuilder {
    private static final String CONSTRUCTOR_ERROR_FORMAT = "Custom runner class %s should have a public constructor with signature %s(Class testClass)";

    private RunnerBuilder fSuiteBuilder;

    public AnnotatedBuilder(RunnerBuilder suiteBuilder) {
        fSuiteBuilder = suiteBuilder;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Exception {
        RunWith annotation = testClass.getAnnotation(RunWith.class);
        if (annotation != null) {
            return buildRunner(annotation.value(), testClass);
        }
        return null;
    }

    public Runner buildRunner(Class<? extends Runner> runnerClass, Class<?> testClass) throws Exception {
        try {
            Constructor<? extends Runner> runner = runnerClass.getConstructor(Class.class);
            return runner.newInstance(testClass);
        } catch (NoSuchMethodException e) {
            try {
                Constructor<? extends Runner> runner = runnerClass.getConstructor(Class.class, RunnerBuilder.class);
                return runner.newInstance(testClass, fSuiteBuilder);
            } catch (NoSuchMethodException e2) {
                String simpleName = runnerClass.getSimpleName();
                throw new InitializationError(String.format(CONSTRUCTOR_ERROR_FORMAT, simpleName, simpleName));
            }
        }
    }
}