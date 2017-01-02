package org.junit.runner;

import static org.junit.internal.Checks.notNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ClassBasedDescriptionBuilder extends DescriptionBuilder<ClassBasedDescriptionBuilder> {
    private final Class<?> testClass;

    ClassBasedDescriptionBuilder(final Class<?> testClass) {
        this.testClass = notNull(testClass, "testClass cannot be null");
        super.displayName = testClass.getName();
        super.uniqueId = testClass.getCanonicalName();
        super.annotations = new ArrayList<Annotation>(Arrays.asList(testClass.getAnnotations()));
    }

    /**
     * Create a {@code ImmutableDescription} representing a suite for the current state of the {@code DescriptionBuilder}.
     *
     * @param children the children of this suite
     * @return a {@code ImmutableDescription} represented by the {@code DescriptionBuilder}
     */
    public <T extends ImmutableDescription> ImmutableDescription createSuiteDescription(List<T> children) {
        return new SuiteDescription(this, notNull(children));
    }

    @Override
    Class<?> getTestClass() {
        return testClass;
    }
}