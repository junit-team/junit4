package org.junit.tests.running.methods;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class InheritedTestTest {
	public abstract static class Super {
		@Test public void nothing() {}
	}
	public static class Sub extends Super {}
	
	@Test public void subclassWithOnlyInheritedTestsRuns() {
		Result result= JUnitCore.runClasses(Sub.class);
		assertTrue(result.wasSuccessful());
	}
} 
