package org.junit.internal.builders;

import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.RunnerBuilder;

public class JUnit4Builder extends RunnerBuilder {
    private final Filter filter;

    public JUnit4Builder(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        return new BlockJUnit4ClassRunner(testClass, filter);
    }
}
