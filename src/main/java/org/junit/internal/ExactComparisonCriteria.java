package org.junit.internal;

import org.junit.Assert;

public class ExactComparisonCriteria extends ComparisonCriteria {
    @Override
    protected void assertElementsEqual(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }
//push down
    protected boolean isArray(Object expected) {
        return (expected instanceof Object[]) || (expected instanceof byte[]) ||
                (expected instanceof short[]) || (expected instanceof int[]) ||
                (expected instanceof long[]) || (expected instanceof float[]) ||
                (expected instanceof double[]) || (expected instanceof boolean[]) ||
                (expected instanceof char[]);
    }

}
