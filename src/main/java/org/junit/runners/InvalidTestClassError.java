package org.junit.runners;

import org.junit.runners.model.InitializationError;

import java.util.List;

public class InvalidTestClassError extends InitializationError {
    private static final long serialVersionUID = 1L;

    private final Class<?> testClass;
    private final String message;

    public InvalidTestClassError(Class<?> offendingTestClass, List<Throwable> errors) {
        super(errors);
        this.testClass = offendingTestClass;
        this.message = createMessage(testClass, errors);
    }

    @Override
    public String getMessage() {
        return message;
    }

    private static String createMessage(Class<?> testClass, List<Throwable> errors) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Invalid test class '%s':", testClass.getName()));
        int i = 1;
        for (Throwable error : errors) {
            sb.append("\n  " + i++ + ". " + error.getMessage());
        }
        return sb.toString();
    }
}
