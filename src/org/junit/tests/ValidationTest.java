package org.junit.tests;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Request;

public class ValidationTest {
	public static class WrongBeforeClass {
		@BeforeClass
		protected int a() {
			return 0;
		}
	}
	
	@Test
	public void initializationErrorIsOnCorrectClass() {
		assertEquals(WrongBeforeClass.class.getName(), 
				Request.aClass(WrongBeforeClass.class).getRunner().getDescription().getDisplayName());
	}
}
