package org.junit.experimental.categories;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runners.model.FrameworkMethod;
import org.junit.validator.AnnotationValidator;

/**
 * Validates that there are no errors in the use of the {@code Category}
 * annotation. If there is, a {@code Throwable} object will be added to the list
 * of errors.
 *
 * @since 4.12
 */
public final class CategoryValidator extends AnnotationValidator {

    @SuppressWarnings("unchecked")
    private static final Set<Class<? extends Annotation>> INCOMPATIBLE_ANNOTATIONS = unmodifiableSet(new HashSet<Class<? extends Annotation>>(
            asList(BeforeClass.class, AfterClass.class, Before.class, After.class)));

    /**
     * Adds to {@code errors} a throwable for each problem detected. Looks for
     * {@code BeforeClass}, {@code AfterClass}, {@code Before} and {@code After}
     * annotations.
     *
     * @param method the method that is being validated
     * @return A list of exceptions detected
     *
     * @since 4.12
     */
    @Override
    public List<Exception> validateAnnotatedMethod(FrameworkMethod method) {
        List<Exception> errors = new ArrayList<Exception>();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            for (Class<?> clazz : INCOMPATIBLE_ANNOTATIONS) {
                if (annotation.annotationType().isAssignableFrom(clazz)) {
                    addErrorMessage(errors, clazz);
                }
            }
        }
        return unmodifiableList(errors);
    }

    private void addErrorMessage(List<Exception> errors, Class<?> clazz) {
        String message = String.format("@%s can not be combined with @Category",
                clazz.getSimpleName());
        errors.add(new Exception(message));
    }
}
