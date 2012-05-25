package org.junit.internal;

import org.junit.Assert;

/**
 * @since 4.7
 */
public class ExactComparisonCriteria extends ComparisonCriteria {
	@Override
	protected void assertElementsEqual(Object expected, Object actual) {
		Assert.assertEquals(expected, actual);
	}
}
