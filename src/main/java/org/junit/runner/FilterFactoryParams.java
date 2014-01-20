package org.junit.runner;

public final class FilterFactoryParams {
    private final String args;

    public FilterFactoryParams(String args) {
        if (args == null) {
            throw new NullPointerException();
        }

        this.args = args;
    }

    public String getArgs() {
        return args;
    }
}
