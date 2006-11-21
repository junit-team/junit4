package org.junit.tests;

import org.junit.Test;
import org.junit.runner.Description;
import static org.junit.Assert.assertFalse;

public class TestDescriptionTest {
	@Test public void equalsIsFalseForNonTestDescription() {
		assertFalse(Description.createTestDescription(getClass(), "a").equals(new Integer(5)));
	}
}
