package org.junit.runner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for {@link ImmutableDescription} instances.
 *
 * @see {@code Description}
 * @since 4.12
 */
public abstract class DescriptionBuilder {
    protected String displayName;
    protected String uniqueId;
    protected List<Annotation> annotations;

    /**
     * Creates a {@code DescriptionBuilder} for {@code testClass}. Additional attributes may be added
     * to the builder before the {@code Description} is finally built using one of the create methods.
     * <p>
     * Defaults:
     * <ul>
     *     <li>Display Name: {@code testClass.getSimpleName()}</li>
     *     <li>Unique ID: {@code testClass.getCanonicalName()}</li>
     *     <li>Annotations: all annotations of the given {@code testClass}</li>
     * </ul>
     *
     * @param testClass A {@link Class} containing tests
     * @return a {@code DescriptionBuilder} for {@code testClass}
     */
    public static ClassBasedDescriptionBuilder forClass(Class<?> testClass) {
        return new ClassBasedDescriptionBuilder(testClass);
    }

    /**
     * Creates a {@code DescriptionBuilder} for the given {@code displayName}. Additional attributes may be
     * added to the builder before the {@code Description} is finally built using one of the create methods.
     * <p>
     * Defaults:
     * <ul>
     *     <li>Display Name: {@code displayName}</li>
     *     <li>Unique ID: {@code displayName}</li>
     *     <li>Annotations: none</li>
     * </ul>
     *
     * @param displayName The display name for this description
     * @return a {@code DescriptionBuilder} for the given {@code displayName}
     */
    public static DescriptionBuilder forName(String displayName) {
        return new NameBasedDescriptionBuilder(notNull(displayName));
    }

    /**
     * Changes the display name to the given value.
     *
     * @param displayName the new display name
     */
    public final DescriptionBuilder withDisplayName(String displayName) {
        if (displayName.length() == 0) {
            throw new IllegalArgumentException("The display name must not be empty");
        }
        this.displayName = displayName;
        return this;
    }

    /**
     * Changes the unique ID to the given value.
     *
     * @param uniqueId the new unique ID
     */
    public final DescriptionBuilder withUniqueId(String uniqueId) {
        this.uniqueId = notNull(uniqueId);
        return this;
    }

    /**
     * Adds additional annotations. These annotations are passed to the {@code Description} for downstream interpreters.
     *
     * @param annotations the additional annotations
     */
    public final DescriptionBuilder withAdditionalAnnotations(List<Annotation> annotations) {
        if (annotations.contains(null)) {
            throw new NullPointerException("Cannot add a null annotation");
        }
        this.annotations.addAll(annotations);
        return this;
    }

    /**
     * Adds additional annotations. These annotations are passed to the {@code Description} for downstream interpreters.
     *
     * @param annotation the first additional annotation
     * @param additionalAnnotations more additional annotations
     */
    public final DescriptionBuilder withAdditionalAnnotations(Annotation annotation, Annotation... additionalAnnotations) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        annotations.add(annotation);
        annotations.addAll(Arrays.asList(additionalAnnotations));
        return withAdditionalAnnotations(annotations);
    }

    /**
     * Create a {@code ImmutableDescription} representing a suite for the current state of the {@code DescriptionBuilder}.
     *
     * @param children the children of this suite
     * @return a {@code ImmutableDescription} represented by the {@code DescriptionBuilder}
     */
    public final <T extends ImmutableDescription> ImmutableDescription createSuiteDescription(List<T> children) {
        return new SuiteDescription(this, notNull(children));
    }

    /**
     * Create a {@code ImmutableDescription} representing a test for the current state of the {@code DescriptionBuilder}.
     *
     * @return a {@code ImmutableDescription} represented by the {@code DescriptionBuilder}
     */
    public final ImmutableDescription createTestDescription() {
        return new TestDescription(this);
    }

    abstract Class<?> getTestClass();

    private static <T> T notNull(T value) {
        if (value == null) {
            throw new NullPointerException();
        }
        return value;
    }
}
