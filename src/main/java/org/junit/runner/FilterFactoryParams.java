package org.junit.runner;

/**
 * Parameters to a {@link FilterFactory}.
 */
public final class FilterFactoryParams {
    private final String args;

    /**
     * Constructs a {@link FilterFactoryParams}.
     *
     */
    public FilterFactoryParams() {
        this(null);
    }

    /**
     * Constructs a {@link FilterFactoryParams}.
     *
     * @param args Arguments
     */
    public FilterFactoryParams(String args) {
        this.args = args;
    }

    /**
     * Returns the arguments.
     */
    public String getArgs() {
        return args;
    }
}
