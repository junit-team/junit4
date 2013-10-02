package org.junit.runner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

class NameBasedDescriptionBuilder extends DescriptionBuilder {
    NameBasedDescriptionBuilder(String displayName) {
        super.displayName = displayName;
        super.uniqueId = displayName;
        super.annotations = new ArrayList<Annotation>();
    }

    @Override
    public <T extends ImmutableDescription> ImmutableDescription createSuiteDescription(List<T> children) {
        return new SuiteDescription(null, displayName, uniqueId, annotations.toArray(ANNOTATIONS_TYPE), children);
    }

    @Override
    public ImmutableDescription createTestDescription() {
        final String name = String.format("%s(%s)", displayName, uniqueId);
        return new TestDescription(null, name, name, annotations.toArray(ANNOTATIONS_TYPE));
    }
}