package org.junit.internal.builders;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;

public class IgnoredClassRunner extends Runner implements Filterable {
    private final Class<?> fTestClass;

    public IgnoredClassRunner(Class<?> testClass) {
        fTestClass = testClass;
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.fireTestIgnored(getDescription());
    }

    @Override
    public Description getDescription() {
        return Description.createSuiteDescription(fTestClass);
    }

    /**
     * Throws an exception as this class never has children.
     *
     * @param filter the {@link Filter} to apply
     * @throws NoTestsRemainException
     */
    public void filter(Filter filter) throws NoTestsRemainException {
        throw new NoTestsRemainException();
    }
}