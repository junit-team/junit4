package org.junit.internal;

import org.junit.Assert;

public class InexactComparisonCriteria extends ComparisonCriteria {
	public double fDelta;

	public InexactComparisonCriteria(double delta) {
		fDelta= delta;
	}

	// TODO (Apr 29, 2009 4:43:46 PM): Look for missing JavaDoc
	@Override
	protected void assertElementsEqual(Object expected, Object actual) {
		if (expected instanceof Double)
			Assert.assertEquals((Double)expected, (Double)actual, fDelta);
		else
			Assert.assertEquals((Float)expected, (Float)actual, fDelta);
	}
}