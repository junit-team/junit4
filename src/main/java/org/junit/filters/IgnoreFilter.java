package org.junit.filters;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class IgnoreFilter extends Filter {
    @Override
    public boolean shouldRun(final Description description) {
        return description.getAnnotation(Ignore.class) == null;
    }

    @Override
    public String describe() {
        return "IgnoreFilter";
    }
}
