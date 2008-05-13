package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class EnclosedTest {
	@RunWith(Enclosed.class)
	public static class Enclosing {
		public static class A {
			@Test public void a() {}
			@Test public void b() {}
		}
		public static class B {
			@Test public void a() {}
			@Test public void b() {}
			@Test public void c() {}
		}
	}
	
	@Test public void enclosedRunnerPlansEnclosedClasses() throws Exception {
		Runner runner= Request.aClass(Enclosing.class).getRunner();
		assertEquals(5, runner.testCount());
	}
	
	@Test public void enclosedRunnerRunsEnclosedClasses() throws Exception {
		Result result= JUnitCore.runClasses(Enclosing.class);
		assertEquals(5, result.getRunCount());
	}
	
	@Test public void enclosedRunnerIsNamedForEnclosingClass() throws Exception {
		assertEquals(Enclosing.class.getName(), Request.aClass(Enclosing.class)
				.getRunner().getDescription().getDisplayName());
	}
}
