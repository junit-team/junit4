package org.junit.runner;

/**
 * Parameters to a {@link FilterFactory}.
 */
public final class FilterFactoryParams {
    private final Description description;
    private final String args;

    /**
     * Constructs a {@link FilterFactoryParams}.
     *
     * @param description {@link Description}
     */
    public FilterFactoryParams(Description description) {
        this(description, null);
    }

    /**
     * Constructs a {@link FilterFactoryParams}.
     *
     * @param description {@link Description}
     * @param args Arguments
     */
    public FilterFactoryParams(Description description, String args) {
        this.description = description;
        this.args = args;
    }

    /**
     * Returns the {@link Description}.
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Returns the arguments.
     */
    public String getArgs() {
        return args;
    }
}
