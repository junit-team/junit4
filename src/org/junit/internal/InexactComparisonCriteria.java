package org.junit.internal;

import org.junit.Assert;

public class InexactComparisonCriteria extends ComparisonCriteria {
	public double fDelta;

	public InexactComparisonCriteria(double delta) {
		fDelta= delta;
	}

	@Override
	protected void assertElementsEqual(Object expected, Object actual) {
		if (expected instanceof Double)
			Assert.assertEquals((Double)expected, (Double)actual, fDelta);
		else
			Assert.assertEquals((Float)expected, (Float)actual, fDelta);
	}
}