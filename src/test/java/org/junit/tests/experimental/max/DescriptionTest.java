package org.junit.tests.experimental.max;

import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.Description;

public class DescriptionTest {

	@Test
	public void parseClass_whenCantParse() {
		assertNull(Description.TEST_MECHANISM.getTestClass());
	}

	@Test
	public void parseMethod_whenCantParse() {
		assertNull(Description.TEST_MECHANISM.getMethodName());
	}

	@Test(expected= IllegalArgumentException.class)
	public void createSuiteDescription_whenZeroLength() {
		Description.createSuiteDescription("");
	}
	
}
