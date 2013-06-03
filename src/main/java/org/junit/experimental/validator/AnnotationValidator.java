package org.junit.experimental.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Provides an interface with callback methods to validate annotations.
 * Implementations are attached to the {@code org.junit.experimental.validator.ValidateWith}
 * annotation.
 */
public interface AnnotationValidator {
    /**
     * Validates annotation on the given class.
     *
     * @param type that is being validated
     * @param errors A throwable will be added for each error
     */
    void validateAnnotatedClass(Class<?> type, List<Throwable> errors);

    /**
     * Validates annotation on the given field.
     *
     * @param field that is being validated
     * @param errors A throwable will be added for each error
     */
    void validateAnnotatedField(Field field,  List<Throwable> errors);

    /**
     * Validates annotation on the given method.
     *
     * @param method that is being validated
     * @param errors A throwable will be added for each error
     */
    void validateAnnotatedMethod(Method method, List<Throwable> errors);
}
