package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects values from fields in a class. Subclasses can filter which fields
 * are collected and transform their values. Values are collected by finding all
 * fields that have a declared type that is assignable to the type parameter of
 * this class (for instance, a {@code FieldsValueCollector<MethodRule>} will
 * only call fields that are declared to have a type that is an instance of
 * {@code MethodRule}).
 *
 * <p>Subclasses must provide a compile-time value for the type parameter {@code T}.
 *
 * <p>For example, here is an object for collecting {@code TestRule} instances from
 * fields annotated with {@code @Rule} that have a return type that is
 * assignable to {@code TestRule}:
 *
 * <pre>
 * private static FieldValueCollector&lt;TestRule&gt; TEST_RULES_FROM_FIELDS
 *     = new FieldValueCollector&lt;TestRule&gt;() {
 * };
 * </pre>
 *
 * @param <T> declared type of the fields to collect
 *
 * @since 4.13
 */
public abstract class FieldValueCollector<T> {
    private final Class<T> fieldType;

    FieldValueCollector(Class<T> fieldType) {
        this.fieldType = fieldType;
    }

    @SuppressWarnings("unchecked")
    protected FieldValueCollector() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        Type type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        fieldType = (Class<T>) type;
    }

    /**
     * Determines whether the field;s value should be included in the output.
     * Subclasses can override this to filter out values. By default, always
     * returns {@code true}.
     */
    protected boolean includeValue(FrameworkField field) {
        return true;
    }

    /**
     * Processes the value that came from reading the field. Subclasses can
     * override this to wrap the value, to inspect the value or to make method
     * calls on the value. By default, returns the value.
     */
    protected T processValue(FrameworkField field, T value) {
        return value;
    }

    /**
     * Collects values by reading all fields on the given object annotated by the given annotation.
     *
     * @return list of collected values.
     */
    public final List<T> getValues(
            TestClass testClass, Object test, Class<? extends Annotation> annotationClass) {
        List<T> results = new ArrayList<T>();
        for (FrameworkField each : testClass.getAnnotatedFields(annotationClass)) {
            try {
                Object fieldValue = each.get(test);
                if (fieldType.isInstance(fieldValue) && includeValue(each)) {
                    results.add(processValue(each, fieldType.cast(fieldValue)));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "How did getFields return a field we couldn't access?", e);
            }
        }
        return results;
    }
}
