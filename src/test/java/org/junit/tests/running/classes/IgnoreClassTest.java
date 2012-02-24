package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class IgnoreClassTest {
	@Ignore("For a good reason") public static class IgnoreMe {
		@Test public void iFail() {
			fail();
		}
		
		@Test public void iFailToo() {
			fail();
		}
	}
	
	@Test public void ignoreClass() {
		Result result= JUnitCore.runClasses(IgnoreMe.class);
		assertEquals(0, result.getFailureCount());
		assertEquals(1, result.getIgnoreCount());
	}
}
