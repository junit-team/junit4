package org.junit.runner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ClassBasedDescriptionBuilder extends DescriptionBuilder {
    private final Class<?> testClass;

    ClassBasedDescriptionBuilder(final Class<?> testClass) {
        this.testClass = testClass;
        super.displayName = testClass.getSimpleName();
        super.uniqueId = testClass.getCanonicalName();
        super.annotations = new ArrayList<Annotation>(Arrays.asList(testClass.getAnnotations()));
    }

    @Override
    public <T extends ImmutableDescription> ImmutableDescription createSuiteDescription(List<T> children) {
        return new SuiteDescription(testClass, displayName, uniqueId, annotations.toArray(ANNOTATIONS_TYPE), children);
    }

    @Override
    public ImmutableDescription createTestDescription() {
        final String name = String.format("%s(%s)", displayName, uniqueId);
        return new TestDescription(testClass, name, name, annotations.toArray(ANNOTATIONS_TYPE));
    }
}