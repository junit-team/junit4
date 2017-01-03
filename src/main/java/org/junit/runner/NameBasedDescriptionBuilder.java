package org.junit.runner;

import static org.junit.internal.Checks.notNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public final class NameBasedDescriptionBuilder extends DescriptionBuilder<NameBasedDescriptionBuilder> {

    NameBasedDescriptionBuilder(String displayName) {
        super.displayName = notNull(displayName, "displayName cannot be null");
        super.uniqueId = displayName;
        super.annotations = new ArrayList<Annotation>();
    }

    /**
     * Create a {@code ImmutableDescription} representing a test for the current state of the {@code DescriptionBuilder}.
     *
     * @return a {@code ImmutableDescription} represented by the {@code DescriptionBuilder}
     */
    public ImmutableDescription createTestDescription() {
        return new TestDescription(this, null, null);
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
        return null;
    }
}
