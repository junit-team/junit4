package org.junit.tests;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.MixIn;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class MixInTest {
	private static final String GLOBAL_STATE_IS_INVALID= "global state is invalid";

	public static class CustomTest {
		@Before
		public static void assertGlobalStateIsValid(Object test) {
			Assert.fail(GLOBAL_STATE_IS_INVALID);
		}
	}

	@MixIn(CustomTest.class)
	public static class UsesGlobalState {
		@Test
		public void foo() {
		}
	}	
	
	@Test
	public void failsWithGlobalState() {
		Result result= JUnitCore.runClasses(UsesGlobalState.class);
		assertEquals(1, result.getFailureCount());
		assertEquals(GLOBAL_STATE_IS_INVALID, result.getFailures().get(0)
				.getMessage());
	}

	public static class ExtraTest {
		@Test
		public static void bar(Object test) {
		}
	}
	
	@MixIn({CustomTest.class, ExtraTest.class})
	public static class Combined {
		
	}
	
	@Test
	public void extendWithMultipleClasses() {
		Result result= JUnitCore.runClasses(UsesGlobalState.class);
		assertEquals(1, result.getFailureCount());
		assertEquals(GLOBAL_STATE_IS_INVALID, result.getFailures().get(0)
				.getMessage());
	}


}
