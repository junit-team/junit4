package org.junit.validator;

import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import static java.util.Collections.emptyList;

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
     * @param testClass that is being validated
     * @return A list of exceptions. Default behavior is to return an empty list.
     *
     * @since 4.12
     */
    public List<Exception> validateAnnotatedClass(TestClass testClass) {
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
    public List<Exception> validateAnnotatedField(FrameworkField field) {
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
    public List<Exception> validateAnnotatedMethod(FrameworkMethod method) {
        return NO_VALIDATION_ERRORS;
    }
}
