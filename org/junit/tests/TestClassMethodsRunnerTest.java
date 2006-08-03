package org.junit.tests;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestClassMethodsRunnerTest  {
	
	public static class FaultyConstructor {
		public FaultyConstructor() throws Exception {
			throw new Exception("Thrown during construction");
		}
		
		@Test public void someTest() {
			/* Empty test just to fool JUnit and IDEs into running 
			 * this class as a JUnit test */
		}
	};
	
	public static class NoTests {
		//class without tests
	}
	
	@Test public void constructorException() {
		JUnitCore core = new JUnitCore();
		Result result = core.run(new Class[] {FaultyConstructor.class});
		Failure failure = result.getFailures().get(0);
		assertEquals("Thrown during construction", failure.getException().getMessage());
	}
	
	@Test public void noRunnableMethods() {
		JUnitCore core = new JUnitCore();
		Result result = core.run(new Class[] {NoTests.class});
		Failure failure = result.getFailures().get(0);
		assertEquals("No runnable methods", failure.getException().getMessage());
	}
}


