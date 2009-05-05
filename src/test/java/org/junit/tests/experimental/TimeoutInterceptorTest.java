package org.junit.tests.experimental;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.experimental.interceptor.Interceptor;
import org.junit.experimental.interceptor.Interceptors;
import org.junit.experimental.interceptor.StatementInterceptor;
import org.junit.experimental.interceptor.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;

public class TimeoutInterceptorTest {
	@RunWith(Interceptors.class)
	public static class HasGlobalTimeout {
		public static String log;
		
		@Interceptor public StatementInterceptor globalTimeout = new Timeout(20);
		
		@Test public void testInfiniteLoop() {
			log+= "ran";
			for(;;) {}
		}
	}
	
	@Test(timeout=100) public void globalTimeoutAvoidsInfiniteLoop() {
		HasGlobalTimeout.log = "";
		Result result= JUnitCore.runClasses(HasGlobalTimeout.class);
		assertEquals(1, result.getFailureCount());
		assertEquals("ran", HasGlobalTimeout.log);
	}
}
