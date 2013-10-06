package org.junit.experimental.categories;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.validator.AnnotationValidator;

/**
 * Validates that there are no errors in the use of the {@code Category}
 * annotation. If there is, a {@code Throwable} object will be added to the list
 * of errors.
 *
 * @since 4.12
 */
public final class CategoryValidator extends AnnotationValidator {

    private static Set<Class<? extends Annotation>> fIncompatibleAnnotations = buildIncompatibleAnnotationsSet();

    /**
     * Adds to {@code errors} a throwable for each problem detected. Looks for
     * {@code BeforeClass}, {@code AfterClass}, {@code Before} and {@code After}
     * annotations.
     *
     * @param method the method that is being validated
     * @return A list of errors detected
     *
     * @since 4.12
     */
    @Override
    public List<Throwable> validateAnnotatedMethod(Method method) {
        List<Throwable> errors = new ArrayList<Throwable>();
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            for (Class clazz : fIncompatibleAnnotations) {
                if (annotation.annotationType().isAssignableFrom(clazz)) {
                    addErrorMessage(errors, clazz);
                }
            }
        }
        return Collections.unmodifiableList(errors);
    }

    private static Set<Class<? extends Annotation>> buildIncompatibleAnnotationsSet() {
        Set<Class<? extends Annotation>> incompatibleAnnotations = new HashSet<Class<? extends Annotation>>();
        incompatibleAnnotations.add(BeforeClass.class);
        incompatibleAnnotations.add(AfterClass.class);
        incompatibleAnnotations.add(Before.class);
        incompatibleAnnotations.add(After.class);
        return Collections.unmodifiableSet(incompatibleAnnotations);
    }

    private void addErrorMessage(List<Throwable> errors, Class clazz) {
        String message = String.format("@%s can not be combined with @Category",
                clazz.getSimpleName());
        errors.add(new Throwable(message));
    }
}
