package org.junit.runner;

import static org.junit.internal.Checks.notNull;

import java.util.List;

public final class ClassBasedDescriptionBuilder extends DescriptionBuilder<ClassBasedDescriptionBuilder> {
    private final Class<?> testClass;

    ClassBasedDescriptionBuilder(final Class<?> testClass) {
        super(notNull(testClass, "testClass cannot be null").getAnnotations());
        this.testClass = testClass;
        super.displayName = testClass.getName();
        super.uniqueId = testClass.getCanonicalName();
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