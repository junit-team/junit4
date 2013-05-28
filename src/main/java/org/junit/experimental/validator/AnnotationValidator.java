package org.junit.experimental.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Provides an interface with callback methods to validate annotations.
 * Implementations are attached to the {@code org.junit.experimental.validator.Validator}
 * annotation.
 */
public interface AnnotationValidator {
    /**
     * Validates annotations on the passed in class.
     *
     * @param type that is being validated
     * @param errors A throwable will be added for each error
     */
    void validateAnnotatedClass(Class<?> type, List<Throwable> errors);

    /**
     * Validates annotations on the passed in field.
     *
     * @param field that is being validated
     * @param errors A throwable will be added for each error
     */
    void validateAnnotatedField(Field field,  List<Throwable> errors);

    /**
     * Validates annotations on the passed in method.
     *
     * @param method that is being validated
     * @param errors A throwable will be added for each error
     */
    void validateAnnotatedMethod(Method method, List<Throwable> errors);
}
