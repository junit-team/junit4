package org.junit.tests.experimental.interceptor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.interceptor.Interceptor;
import org.junit.experimental.interceptor.StatementInterceptor;
import org.junit.experimental.interceptor.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TimeoutInterceptorTest {
	public static class HasGlobalTimeout {
		public static String log;
		
		@Interceptor public StatementInterceptor globalTimeout = new Timeout(20);
		
		@Test public void testInfiniteLoop1() {
			log+= "ran1";
			for(;;) {}
		}
		
		@Test public void testInfiniteLoop2() {
			log+= "ran2";
			for(;;) {}
		}
	}
	
	// TODO (May 6, 2009 11:30:19 AM): Make a separate build that makes gump happy
	@Ignore("For gump, for now")
	@Test(timeout=100) public void globalTimeoutAvoidsInfiniteLoop() {
		HasGlobalTimeout.log = "";
		Result result= JUnitCore.runClasses(HasGlobalTimeout.class);
		assertEquals(2, result.getFailureCount());
		assertThat(HasGlobalTimeout.log, containsString("ran1"));
		assertThat(HasGlobalTimeout.log, containsString("ran2"));
	}
}
