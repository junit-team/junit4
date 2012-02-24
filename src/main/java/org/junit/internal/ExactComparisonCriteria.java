package org.junit.internal;

import org.junit.Assert;

public class ExactComparisonCriteria extends ComparisonCriteria {
	@Override
	protected void assertElementsEqual(Object expected, Object actual) {
		Assert.assertEquals(expected, actual);
	}
}
