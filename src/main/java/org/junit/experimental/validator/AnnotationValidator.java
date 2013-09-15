package org.junit.experimental.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Validates annotations on classes and methods. To be validated,
 * an annotation should be annotated with {@link ValidateWith}
 *
 * Instances of this class are shared by multiple test runners, so they should
 * be immutable and thread-safe.
 */
public class AnnotationValidator {
    /**
     * Validates annotation on the given class.
     *
     * @param type that is being validated
     * @param errors A throwable will be added for each error
     */
    public void validateAnnotatedClass(Class<?> type, List<Throwable> errors) {

    }

    /**
     * Validates annotation on the given field.
     *
     * @param field that is being validated
     * @param errors A throwable will be added for each error
     */
    public void validateAnnotatedField(Field field,  List<Throwable> errors) {

    }

    /**
     * Validates annotation on the given method.
     *
     * @param method that is being validated
     * @param errors A throwable will be added for each error
     */
    public void validateAnnotatedMethod(Method method, List<Throwable> errors) {

    }
}
