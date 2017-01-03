package org.junit.runner;

import static org.junit.internal.Checks.checkArgument;
import static org.junit.internal.Checks.notNull;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Builder for {@link ImmutableDescription} instances.
 *
 * @see {@code Description}
 * @since 4.12
 */
public abstract class DescriptionBuilder<B extends DescriptionBuilder<B>> {
    static Collection<Annotation> NO_ANNOTATIONS = Collections.emptyList();

    protected String displayName;
    protected Serializable uniqueId;
    protected final LinkedHashSet<Annotation> annotations;

    DescriptionBuilder(Collection<Annotation> annotations) {
        this.annotations = new LinkedHashSet<Annotation>(annotations);
    }

    DescriptionBuilder(Annotation[] annotations) {
        this(Arrays.asList(annotations));
    }

    /**
     * Creates a {@code DescriptionBuilder} for {@code testClass}. Additional attributes may be added
     * to the builder before the {@code Description} is finally built using one of the create methods.
     *
     * <p>Defaults:
     * <ul>
     *     <li>Display Name: {@code testClass.getName()}</li>
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
     * Creates a {@code DescriptionBuilder} for {@code method}. Additional attributes may be added
     * to the builder before the {@code Description} is finally built using one of the create methods.
     *
     * <p>Defaults:
     * <ul>
     *     <li>Display Name: {@code String.format("%s(%s)", method.getName(), testClass.getName())}</li>
     *     <li>Unique ID: {@code String.format("%s(%s)", method.getName(), testClass.getName())}</li>
     *     <li>Annotations: all annotations of the given {@code method}</li>
     * </ul>
     *
     * @param testClass A {@link Class} containing tests
     * @param method A {@link Method} in {@code testClass}
     * @return a {@code DescriptionBuilder} for {@code testClass}
     */
    public static MethodBasedDescriptionBuilder forMethod(Class<?> testClass, Method method) {
        return new MethodBasedDescriptionBuilder(testClass, method);
    }

    /**
     * Creates a {@code DescriptionBuilder} for a named method in {@code testClass}. Additional attributes may be added
     * to the builder before the {@code Description} is finally built using one of the create methods.
     *
     * <p>Defaults:
     * <ul>
     *     <li>Display Name: {@code String.format("%s(%s)", methodName, testClass.getName())}</li>
     *     <li>Unique ID: {@code String.format("%s(%s)", methodName, testClass.getName())}</li>
     *     <li>Annotations: none</li>
     * </ul>
     *
     * @param testClass A {@link Class} containing tests
     * @param method A {@link Method} in {@code testClass}
     * @return a {@code DescriptionBuilder} for {@code testClass}
     */
    public static MethodBasedDescriptionBuilder forMethod(Class<?> testClass, String methodName) {
        return new MethodBasedDescriptionBuilder(testClass, methodName);
    }
 
    /**
     * Creates a {@code DescriptionBuilder} for a named method in a named class. Additional attributes may be added
     * to the builder before the {@code Description} is finally built using one of the create methods.
     *
     * <p>Defaults:
     * <ul>
     *     <li>Display Name: {@code String.format("%s(%s)", methodName, testClass.getName())}</li>
     *     <li>Unique ID: {@code String.format("%s(%s)", methodName, testClass.getName())}</li>
     *     <li>Annotations: none</li>
     * </ul>
     *
     * @param testClass A {@link Class} containing tests
     * @param method A {@link Method} in {@code testClass}
     * @return a {@code DescriptionBuilder} for {@code testClass}
     */
    public static MethodBasedDescriptionBuilder forMethod(String testClassName, String methodName) {
        return new MethodBasedDescriptionBuilder(testClassName, methodName);
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
    public static NameBasedDescriptionBuilder forName(String displayName) {
        return new NameBasedDescriptionBuilder(displayName);
    }

    /**
     * Changes the display name to the given value.
     *
     * @param displayName the new display name
     */
    public final B withDisplayName(String displayName) {
        checkArgument(displayName.length() > 0, "The display name must not be empty");
        this.displayName = displayName;
        return self();
    }

    /**
     * Changes the unique ID to the given value.
     *
     * @param uniqueId the new unique ID
     */
    public final B withUniqueId(Serializable uniqueId) {
        this.uniqueId = notNull(uniqueId);
        return self();
    }

    /**
     * Adds additional annotations. These annotations are passed to the {@code Description} for downstream interpreters.
     *
     * @param annotations the additional annotations
     */
    public final B withAdditionalAnnotations(List<Annotation> annotations) {
        if (annotations.contains(null)) {
            throw new NullPointerException("Cannot add a null annotation");
        }
        this.annotations.addAll(annotations);
        return self();
    }

    /**
     * Adds additional annotations. These annotations are passed to the {@code Description} for downstream interpreters.
     *
     * @param annotation the first additional annotation
     * @param additionalAnnotations more additional annotations
     */
    public final B withAdditionalAnnotations(Annotation annotation, Annotation... additionalAnnotations) {
        List<Annotation> annotations = new ArrayList<Annotation>();
        annotations.add(annotation);
        annotations.addAll(Arrays.asList(additionalAnnotations));
        return withAdditionalAnnotations(annotations);
    }

    /** If this describes a method invocation, gets the class of the test instance (otherwise, {@code null}. */
    abstract Class<?> getTestClass();

    @SuppressWarnings("unchecked")
    private final B self() {
        return (B) this;
    }
}
