package org.junit.runner;

/**
 * Parameters to a {@link FilterFactory}.
 */
public final class FilterFactoryParams {
    private final Description description;
    private final String args;

    public FilterFactoryParams(Description description) {
        this(description, null);
    }

    public FilterFactoryParams(Description description, String args) {
        this.description = description;
        this.args = args;
    }

    public Description getDescription() {
        return description;
    }

    public String getArgs() {
        return args;
    }
}
