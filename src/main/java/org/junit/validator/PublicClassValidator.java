package org.junit.validator;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.List;

import org.junit.runners.model.TestClass;

/**
 * Validates that a {@link TestClass} is public.
 * 
 * @since 4.12
 */
public class PublicClassValidator implements TestClassValidator {
    private static final List<Exception> NO_VALIDATION_ERRORS = emptyList();

    /**
     * Validate that the specified {@link TestClass} is public.
     * 
     * @param testClass the {@link TestClass} that is validated.
     * @return an empty list if the class is public or a list with a single
     *         exception otherwise.
     */
    public List<Exception> validateTestClass(TestClass testClass) {
        if (testClass.isPublic()) {
            return NO_VALIDATION_ERRORS;
        } else {
            return singletonList(new Exception("The class "
                    + testClass.getName() + " is not public."));
        }
    }
}
