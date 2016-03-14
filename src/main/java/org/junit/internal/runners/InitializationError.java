package org.junit.internal.runners;

import java.util.Arrays;
import java.util.List;

/**
 * Use the published version:
 * {@link org.junit.runners.model.InitializationError}
 * This may disappear as soon as 1 April 2009
 */
@Deprecated
public class InitializationError extends Exception {
    private static final long serialVersionUID = 1L;

    /*
     * We have to use the f prefix until the next major release to ensure
     * serialization compatibility. 
     * See https://github.com/junit-team/junit/issues/976
     */
    private final List<Throwable> fErrors;

    public InitializationError(List<Throwable> errors) {
        this.fErrors = errors;
    }

    public InitializationError(Throwable... errors) {
        this(Arrays.asList(errors));
    }

    public InitializationError(String string) {
        this(new Exception(string));
    }

    public List<Throwable> getCauses() {
        return fErrors;
    }
}
