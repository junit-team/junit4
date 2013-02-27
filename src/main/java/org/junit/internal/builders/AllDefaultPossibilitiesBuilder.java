package org.junit.internal.builders;

import java.util.Arrays;
import java.util.List;

import org.junit.filters.IgnoreFilter;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.RunnerBuilder;

public class AllDefaultPossibilitiesBuilder extends RunnerBuilder {
    private final boolean fCanUseSuiteMethod;
    private final Filter fFilter;

    public AllDefaultPossibilitiesBuilder(boolean canUseSuiteMethod) {
        this(canUseSuiteMethod, new IgnoreFilter());
    }

    public AllDefaultPossibilitiesBuilder(Filter filter) {
        this(true, filter);
    }

    public AllDefaultPossibilitiesBuilder(boolean canUseSuiteMethod, Filter filter) {
        fCanUseSuiteMethod = canUseSuiteMethod;
        fFilter = filter;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        final Description description = Description.createSuiteDescription(testClass);

        if (!fFilter.shouldRun(description)) {
            return new IgnoredClassRunner(testClass);
        }

        List<RunnerBuilder> builders = Arrays.asList(
                annotatedBuilder(),
                suiteMethodBuilder(),
                junit3Builder(),
                junit4Builder());

        for (RunnerBuilder each : builders) {
            Runner runner = each.safeRunnerForClass(testClass);
            if (runner != null) {
                return runner;
            }
        }

        return null;
    }

    protected JUnit4Builder junit4Builder() {
        return new JUnit4Builder(fFilter);
    }

    protected JUnit3Builder junit3Builder() {
        return new JUnit3Builder();
    }

    protected AnnotatedBuilder annotatedBuilder() {
        return new AnnotatedBuilder(this);
    }

    protected IgnoredBuilder ignoredBuilder() {
        return new IgnoredBuilder();
    }

    protected RunnerBuilder suiteMethodBuilder() {
        if (fCanUseSuiteMethod) {
            return new SuiteMethodBuilder();
        }
        return new NullBuilder();
    }
}
