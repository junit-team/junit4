package org.junit.internal;

import org.junit.Assert;

public class InexactComparisonCriteria extends ComparisonCriteria {
    public Object delta;

    public InexactComparisonCriteria(double delta) {
        this.delta = delta;
    }

    public InexactComparisonCriteria(float delta) {
        this.delta = delta;
    }

    @Override
    protected void assertElementsEqual(Object expected, Object actual) {
        if (expected instanceof Double) {
            Assert.assertEquals((Double) expected, (Double) actual, (Double) delta);
        } else {
            Assert.assertEquals((Float) expected, (Float) actual, (Float) delta);
        }
    }
}