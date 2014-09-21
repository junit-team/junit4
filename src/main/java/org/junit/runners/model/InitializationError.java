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

    /*
     * We have to use the f prefix until the next major release to ensure
     * serialization compatibility. 
     * See https://github.com/junit-team/junit/issues/976
     */
    private final List<Throwable> fErrors;

    /**
     * Construct a new {@code InitializationError} with one or more
     * errors {@code errors} as causes
     */
    public InitializationError(List<Throwable> errors) {
        this.fErrors = errors;
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
