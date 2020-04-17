package org.junit.runner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.runners.model.TestClass;
import org.junit.validator.AnnotationValidator;

/**
 * Validates that there are no errors in the use of the {@code OrderWith}
 * annotation. If there is, a {@code Throwable} object will be added to the list
 * of errors.
 *
 * @since 4.13
 */
public final class OrderWithValidator extends AnnotationValidator {

    /**
     * Adds to {@code errors} a throwable for each problem detected. Looks for
     * {@code FixMethodOrder} annotations.
     *
     * @param testClass that is being validated
     * @return A list of exceptions detected
     *
     * @since 4.13
     */
    @Override
    public List<Exception> validateAnnotatedClass(TestClass testClass) {
        if (testClass.getAnnotation(FixMethodOrder.class) != null) {
            return singletonList(
                    new Exception("@FixMethodOrder cannot be combined with @OrderWith"));
        }
        return emptyList();
    }
}
