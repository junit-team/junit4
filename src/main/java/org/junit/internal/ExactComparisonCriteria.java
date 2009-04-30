package org.junit.internal;

import org.junit.Assert;

// TODO (Apr 29, 2009 4:17:49 PM): where should this live?
public class ExactComparisonCriteria extends ComparisonCriteria {
	@Override
	protected void assertElementsEqual(Object expected, Object actual) {
		Assert.assertEquals(expected, actual);
	}
}
