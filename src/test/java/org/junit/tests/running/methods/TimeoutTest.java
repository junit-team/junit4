package org.junit.tests.running.methods;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TimeoutTest {
	
	static public class FailureWithTimeoutTest {
		@Test(timeout= 1000) public void failure() {
			fail();
		}
	}
	
	@Test public void failureWithTimeout() throws Exception {
		JUnitCore core= new JUnitCore();
		Result result= core.run(FailureWithTimeoutTest.class);
		assertEquals(1, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		assertEquals(AssertionError.class, result.getFailures().get(0).getException().getClass());
	}

	static public class FailureWithTimeoutRunTimeExceptionTest {
		@Test(timeout= 1000) public void failure() {
			throw new NullPointerException();
		}
	}
	
	@Test public void failureWithTimeoutRunTimeException() throws Exception {
		JUnitCore core= new JUnitCore();
		Result result= core.run(FailureWithTimeoutRunTimeExceptionTest.class);
		assertEquals(1, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		assertEquals(NullPointerException.class, result.getFailures().get(0).getException().getClass());
	}

	static public class SuccessWithTimeoutTest {
		@Test(timeout= 1000) public void success() {			
		}
	}
		
	@Test public void successWithTimeout() throws Exception {
		JUnitCore core= new JUnitCore();
		Result result= core.run(SuccessWithTimeoutTest.class);
		assertEquals(1, result.getRunCount());
		assertEquals(0, result.getFailureCount());
	}

	static public class TimeoutFailureTest {
		@Test(timeout= 100) public void success() throws InterruptedException {			
			Thread.sleep(40000);
		}
	}
	
	@Ignore("was breaking gump")
	@Test public void timeoutFailure() throws Exception {
		JUnitCore core= new JUnitCore();
		Result result= core.run(TimeoutFailureTest.class);
		assertEquals(1, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		assertEquals(InterruptedException.class, result.getFailures().get(0).getException().getClass());
	}
	
	static public class InfiniteLoopTest {
		@Test(timeout= 100) public void failure() {
			infiniteLoop();
		}

		private void infiniteLoop() {
			for(;;)
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
		}
	}
	
	@Test public void infiniteLoop() throws Exception {
		JUnitCore core= new JUnitCore();
		Result result= core.run(InfiniteLoopTest.class);
		assertEquals(1, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		Throwable exception= result.getFailures().get(0).getException();
		assertTrue(exception.getMessage().contains("test timed out after 100 milliseconds"));
	}
	
	static public class ImpatientLoopTest {
		@Test(timeout= 1) public void failure() {
			infiniteLoop();
		}

		private void infiniteLoop() {
			for(;;);
		}
	}
	
	@Ignore("This breaks sporadically with time differences just slightly more than 200ms")
	@Test public void infiniteLoopRunsForApproximatelyLengthOfTimeout() throws Exception {
		// "prime the pump": running these beforehand makes the runtimes more predictable
		//                   (because of class loading?)
		JUnitCore.runClasses(InfiniteLoopTest.class, ImpatientLoopTest.class);
		long longTime= runAndTime(InfiniteLoopTest.class);
		long shortTime= runAndTime(ImpatientLoopTest.class);
		long difference= longTime - shortTime;
		assertTrue(String.format("Difference was %sms", difference), difference < 200);
	}

	private long runAndTime(Class<?> clazz) {
		JUnitCore core= new JUnitCore();
		long startTime= System.currentTimeMillis();
		core.run(clazz);
		long totalTime = System.currentTimeMillis() - startTime;
		return totalTime;
	}

	@Test public void stalledThreadAppearsInStackTrace() throws Exception {
		JUnitCore core= new JUnitCore();
		Result result= core.run(InfiniteLoopTest.class);
		assertEquals(1, result.getRunCount());
		assertEquals(1, result.getFailureCount());
		Throwable exception= result.getFailures().get(0).getException();
		Writer buffer= new StringWriter();
		PrintWriter writer= new PrintWriter(buffer);
		exception.printStackTrace(writer);
		assertThat(buffer.toString(), containsString("infiniteLoop")); // Make sure we have the stalled frame on the stack somewhere
	}

	@Test public void compatibility() {
		TestResult result= new TestResult();
		new JUnit4TestAdapter(InfiniteLoopTest.class).run(result);
		assertEquals(1, result.errorCount());
	}
	
	public static class WillTimeOut {
		static boolean afterWasCalled= false;
		
		@Test(timeout=1) public void test() {
			for(;;)
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// ok, tests are over
				}
		}
		
		@After public void after() {
			afterWasCalled= true;
		}
	}
	
	@Test public void makeSureAfterIsCalledAfterATimeout() {
		JUnitCore.runClasses(WillTimeOut.class);
		assertThat(WillTimeOut.afterWasCalled, is(true));
	}
}
