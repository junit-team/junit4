package org.junit.validator;

import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Validates annotations on classes and methods. To be validated,
 * an annotation should be annotated with {@link ValidateWith}
 *
 * Instances of this class are shared by multiple test runners, so they should
 * be immutable and thread-safe.
 *
 * @since 4.12
 */
public abstract class AnnotationValidator {

    private static final List<Exception> NO_VALIDATION_ERRORS = emptyList();

    /**
     * Validates annotation on the given class.
     *
     * @param type that is being validated
     * @return A list of exceptions. Default behavior is to return an empty list.
     *
     * @since 4.12
     */
    public List<Exception> validateAnnotatedClass(Class<?> type) {
        return NO_VALIDATION_ERRORS;
    }

    /**
     * Validates annotation on the given field.
     *
     * @param field that is being validated
     * @return A list of exceptions. Default behavior is to return an empty list.
     *
     * @since 4.12
     */
    public List<Exception> validateAnnotatedField(Field field) {
        return NO_VALIDATION_ERRORS;

    }

    /**
     * Validates annotation on the given method.
     *
     * @param method that is being validated
     * @return A list of exceptions. Default behavior is to return an empty list.
     *
     * @since 4.12
     */
    public List<Exception> validateAnnotatedMethod(Method method) {
        return NO_VALIDATION_ERRORS;
    }
}
