package org.junit.runners.model;

import java.util.Arrays;
import java.util.List;

/**
 * Represents one or more problems encountered while initializing a Runner
 *
 * @since 4.5
 */
public class InitializationError extends Exception {
    private static final long serialVersionUID = 1L;
    private final List<Throwable> fErrors;

    /**
     * Construct a new {@code InitializationError} with one or more
     * errors {@code errors} as causes
     */
    public InitializationError(List<Throwable> errors) {
        super(getMessage(errors));
        fErrors = errors;
    }
    
    private static String getMessage(List<Throwable> errors) {
        switch(errors.size()) {
            case 0: return null;
            case 1: return errors.get(0).getMessage();
            default:
                StringBuilder cause = new StringBuilder();
                int i = 0;
                for(Throwable error : errors) {
                    String message = error.getMessage();
                    if(message != null) {
                        if(i > 0) {
                            cause.append('\n');
                        }
                        cause.append(i++).append('.').append(' ').append(message);
                    }
                }
                return cause.toString();
        }
    }

    public InitializationError(Throwable error) {
        this(Arrays.asList(error));
    }

    /**
     * Construct a new {@code InitializationError} with one cause
     * with message {@code string}
     */
    public InitializationError(String string) {
        this(new Exception(string));
    }

    /**
     * Returns one or more Throwables that led to this initialization error.
     */
    public List<Throwable> getCauses() {
        return fErrors;
    }
}
