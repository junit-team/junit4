package org.junit.runner;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Like any {@code Description} the {@code ImmutableDescription} describes a test which is to be run or has been run.
 *
 * <p>Until version 4.11 {@code Description} instances were mutable objects. With 4.12 the DescriptionBuilder was
 * introduced that guarantees that all generated descriptions are immutable objects.
 *
 * <p>The constructors of this class are intentionally left package private so that clients cannot create mutable
 * classes that extend this class. All clients need to inherit from {@code SuiteDescription} or {@code TestDescription}.
 *
 * @see org.junit.runner.Description
 * @see org.junit.runner.DescriptionBuilder
 * @since 4.12
 */
public abstract class ImmutableDescription extends Description {

    ImmutableDescription(DescriptionBuilder<?> builder) {
        super(builder);
    }

    @SuppressWarnings("deprecation")
    <T extends ImmutableDescription> ImmutableDescription(DescriptionBuilder<?> builder, List<T> children) {
        this(builder);
        for (ImmutableDescription child : children) {
            super.addChild(child);
        }
    }

    @Override
    public void addChild(Description description) {
        String msg = "This method is not supported for immutable description instances. " +
                "Please refer to %s for more information.";
        throw new UnsupportedOperationException(String.format(msg, DescriptionBuilder.class.getSimpleName()));
    }

    @Override
    public Class<?> getTestClass() {
        return super.fTestClass == null ? null : super.getTestClass();
    }

    @Override
    public String getClassName() {
        return super.fTestClass == null ? null : super.getClassName();
    }

    @Override
    public String getMethodName() {
        return super.fTestClass == null ? null : super.getMethodName();
    }
}