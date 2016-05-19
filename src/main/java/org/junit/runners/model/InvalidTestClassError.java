package org.junit.runners.model;

import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * Thrown by {@link org.junit.runner.Runner}s in case the class under test is not valid.
 * <p>
 * Its message conveniently lists all of the validation errors.
 *
 * @since 4.13
 */
public class InvalidTestClassError extends InitializationError {
    private static final long serialVersionUID = 1L;

    private final Class<?> testClass;
    private final String message;

    public InvalidTestClassError(Class<?> offendingTestClass, List<Throwable> validationErrors) {
        super(validationErrors);
        this.testClass = offendingTestClass;
        this.message = createMessage(testClass, validationErrors);
    }

    private static String createMessage(Class<?> testClass, List<Throwable> validationErrors) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Invalid test class '%s':", testClass.getName()));
        int i = 1;
        for (Throwable error : validationErrors) {
            sb.append("\n  " + (i++) + ". " + error.getMessage());
        }
        return sb.toString();
    }

    /**
     * @return a message with a list of all of the validation errors
     */
    @Override
    public String getMessage() {
        return message;
    }
}
