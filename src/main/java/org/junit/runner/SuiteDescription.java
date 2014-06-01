package org.junit.runner;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * The {@code SuiteDescription} describes a suite of tests which are to be run or have been run.
 * <p>
 * Until version 4.11 {@code Description} instances were mutable objects. With 4.12 the DescriptionBuilder was
 * introduced that guarantees that all generated descriptions are immutable objects.
 *
 * @see org.junit.runner.Description
 * @see org.junit.runner.ImmutableDescription
 * @see org.junit.runner.DescriptionBuilder
 * @since 4.12
 */
class SuiteDescription extends ImmutableDescription {
    <T extends ImmutableDescription> SuiteDescription(Class<?> testClass, String displayName,
            String uniqueId, Annotation[] annotations, List<T> children) {
        super(testClass, displayName, uniqueId, annotations, children);
    }

    @Override
    public boolean isSuite() {
        return true;
    }

    @Override
    public boolean isTest() {
        return false;
    }

    @Override
    public int testCount() {
        int result = 0;
        for (Description child : fChildren) {
            result += child.testCount();
        }
        return result;
    }
}