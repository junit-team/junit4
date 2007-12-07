package org.junit.tests.listening;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TestListenerTest {
	
	int count;
	
	class ErrorListener extends RunListener {
		@Override
		public void testRunStarted(Description description) throws Exception {
			throw new Error();
		}
	}
	
	public static class OneTest {
		@Test public void nothing() {}
	}
	
	@Test(expected=Error.class) public void failingListener() {
		JUnitCore runner= new JUnitCore();
		runner.addListener(new ErrorListener());
		runner.run(OneTest.class);
	}
	
	class ExceptionListener extends ErrorListener {
		@Override
		public void testRunStarted(Description description) throws Exception {
			count++;
			throw new Exception();
		}
	}
	
	@Test public void removeFailingListeners() {
		JUnitCore core= new JUnitCore();
		core.addListener(new ExceptionListener());
		
		count= 0;
		Result result= core.run(OneTest.class);
		assertEquals(1, count);
		assertEquals(1, result.getFailureCount());
		Failure testFailure= result.getFailures().get(0);
		assertEquals(Description.TEST_MECHANISM, testFailure.getDescription());

		count= 0;
		core.run(OneTest.class);
		assertEquals(0, count); // Doesn't change because listener was removed		
	}
	
	@Test public void freshResultEachTime() {
		JUnitCore core= new JUnitCore();
		Result first= core.run(OneTest.class);
		Result second= core.run(OneTest.class);
		assertNotSame(first, second);
	}
}
