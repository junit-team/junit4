package org.junit.experimental.validator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates that there are no errors in the use of the {@code Category}
 * annotation. If there is, a {@code Throwable} object will be added to the list
 * of errors.
 *
 * @since 4.12
 */
public class CategoryValidator implements AnnotationValidator {

    private static Set<Class<?>> fIncompatibleAnnotations = null;

    public void validateAnnotatedClass(Class<?> type, List<Throwable> errors) {
    }

    public void validateAnnotatedField(Field field, List<Throwable> errors) {
    }

    /**
     * Adds to {@code errors} a throwable for each problem detected. Looks for
     * {@code BeforeClass}, {@code AfterClass}, {@code Before} and {@code After}
     * annotations.
     *
     * @param method the method that is being validated
     * @param errors any errors detected are added to this list
     */
    public void validateAnnotatedMethod(Method method, List<Throwable> errors) {
        final Set<Class<?>> incompatibleAnnotations = buildIncompatibleAnnotationsSet();

        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            for (Class clazz : incompatibleAnnotations) {
                if (annotation.annotationType().isAssignableFrom(clazz)) {
                    addErrorMessage(errors, clazz);
                }
            }
        }
    }

    private Set<Class<?>> buildIncompatibleAnnotationsSet() {
        if (fIncompatibleAnnotations == null) {
            fIncompatibleAnnotations = new HashSet<Class<?>>();
            fIncompatibleAnnotations.add(BeforeClass.class);
            fIncompatibleAnnotations.add(AfterClass.class);
            fIncompatibleAnnotations.add(Before.class);
            fIncompatibleAnnotations.add(After.class);
        }
        return fIncompatibleAnnotations;
    }

    private void addErrorMessage(List<Throwable> errors, Class clazz) {
        String message = String.format("@%s can not be combined with @Category",
                clazz.getSimpleName());
        errors.add(new Throwable(message));
    }
}
