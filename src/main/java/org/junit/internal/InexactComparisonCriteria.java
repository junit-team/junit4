package org.junit.internal;

import org.junit.Assert;

/**
 * @since 4.6
 */
public class InexactComparisonCriteria extends ComparisonCriteria {
	public Object fDelta;

	public InexactComparisonCriteria(double delta) {
		fDelta= delta;
	}
	
	public InexactComparisonCriteria(float delta){
		fDelta = delta;
	}

	@Override
	protected void assertElementsEqual(Object expected, Object actual) {
		if (expected instanceof Double)
			Assert.assertEquals((Double)expected, (Double)actual, (Double)fDelta);
		else
			Assert.assertEquals((Float)expected, (Float)actual, (Float)fDelta);
	}
}