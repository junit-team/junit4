package org.junit.filters;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class PassThroughFilter extends Filter {
    @Override
    public boolean shouldRun(final Description description) {
        return true;
    }

    @Override
    public String describe() {
        return "Pass-Through Filter";
    }

    @Override
    public Filter intersect(final Filter second) {
        return second;
    }
}
