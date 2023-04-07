package org.junit.internal;

import org.junit.Assert;

public class InexactComparisonCriteria extends ComparisonCriteria {
    public Object fDelta;

    public InexactComparisonCriteria(double delta) {
        fDelta = delta;
    }

    public InexactComparisonCriteria(float delta) {
        fDelta = delta;
    }

    @Override
    protected void assertElementsEqual(Object expected, Object actual) {
        if (expected instanceof Double) {
            Assert.assertEquals((Double) expected, (Double) actual, (Double) fDelta);
        } else {
            Assert.assertEquals((Float) expected, (Float) actual, (Float) fDelta);
        }
    }
//push down to sub class
    public boolean isArray(Object expected) {
        Class<?> clazz = expected != null ? expected.getClass() : null;
        return clazz != null && clazz.getComponentType() != null;
    }

}