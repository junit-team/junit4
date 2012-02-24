package org.junit.tests.description;

import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.Description;

public class TestDescriptionTest {
	@Test public void equalsIsFalseForNonTestDescription() {
		assertFalse(Description.createTestDescription(getClass(), "a").equals(new Integer(5)));
	}
}