package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TimeoutRuleTest {
	public static class HasGlobalLongTimeout {
		public static String log;
		
		@Rule public TestRule globalTimeout = new Timeout(200);
		
		@Test public void testInfiniteLoop1() {
			log+= "ran1";
			for(;;) {}
		}
		
		@Test public void testInfiniteLoop2() {
			log+= "ran2";
			for(;;) {}
		}
	}
	
	public static class HasGlobalTimeUnitTimeout {
		public static String log;
		
		@Rule public TestRule globalTimeout = new Timeout(200, TimeUnit.MILLISECONDS);
		
		@Test public void testInfiniteLoop1() {
			log+= "ran1";
			for(;;) {}
		}
		
		@Test public void testInfiniteLoop2() {
			log+= "ran2";
			for(;;) {}
		}
	}
	
	@Test(timeout=1000) public void globalLongTimeoutAvoidsInfiniteLoop() {
		HasGlobalLongTimeout.log = "";
		Result result= JUnitCore.runClasses(HasGlobalLongTimeout.class);
		assertEquals(2, result.getFailureCount());
		assertThat(HasGlobalLongTimeout.log, containsString("ran1"));
		assertThat(HasGlobalLongTimeout.log, containsString("ran2"));
	}
	
	@Test(timeout=1000) public void globalTimeUnitTimeoutAvoidsInfiniteLoop() {
		HasGlobalLongTimeout.log = "";
		Result result= JUnitCore.runClasses(HasGlobalTimeUnitTimeout.class);
		assertEquals(2, result.getFailureCount());
		assertThat(HasGlobalTimeUnitTimeout.log, containsString("ran1"));
		assertThat(HasGlobalTimeUnitTimeout.log, containsString("ran2"));
	}
}
