package org.junit.experimental.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * Validates annotations on classes and methods. To be validated,
 * an annotation should be annotated with {@link ValidateWith}
 *
 * Instances of this class are shared by multiple test runners, so they should
 * be immutable and thread-safe.
 */
public abstract class AnnotationValidator {
    /**
     * Validates annotation on the given class.
     *
     * @param type that is being validated
     * @return A list of throwables. Default behavior is to return an empty list.
     */
    public List<Throwable> validateAnnotatedClass(Class<?> type) {
        return Collections.emptyList();
    }

    /**
     * Validates annotation on the given field.
     *
     * @param field that is being validated
     * @return A list of throwables. Default behavior is to return an empty list.
     */
    public List<Throwable> validateAnnotatedField(Field field) {
        return Collections.emptyList();

    }

    /**
     * Validates annotation on the given method.
     *
     * @param method that is being validated
     * @return A list of throwables. Default behavior is to return an empty list.
     */
    public List<Throwable> validateAnnotatedMethod(Method method) {
        return Collections.emptyList();
    }
}
