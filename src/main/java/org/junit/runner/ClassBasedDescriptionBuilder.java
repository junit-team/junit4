package org.junit.runner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;

class ClassBasedDescriptionBuilder extends DescriptionBuilder {
    private final Class<?> testClass;

    ClassBasedDescriptionBuilder(final Class<?> testClass) {
        this.testClass = testClass;
        super.displayName = testClass.getSimpleName();
        super.uniqueId = testClass.getCanonicalName();
        super.annotations = new ArrayList<Annotation>(Arrays.asList(testClass.getAnnotations()));
    }

    @Override
    Class<?> getTestClass() {
        return testClass;
    }
}