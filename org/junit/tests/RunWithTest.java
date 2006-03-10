package org.junit.tests;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class RunWithTest {

	private static String log;

	public static class ExampleRunner extends Runner {
		public ExampleRunner(Class<?> klass) {
			log+= "initialize";
		}

		@Override
		public void run(RunNotifier notifier) {
			log+= "run";
		}

		@Override
		public int testCount() {
			log+= "count";
			return 0;
		}

		@Override
		public Description getDescription() {
			log+= "plan";
			return Description.createSuiteDescription("example");
		}		
	}
	
	@RunWith(ExampleRunner.class)
	public static class ExampleTest {
	}
	
	@Test public void run() {
		log= "";

		JUnitCore.runClasses(ExampleTest.class);
		assertTrue(log.contains("plan"));
		assertTrue(log.contains("initialize"));
		assertTrue(log.contains("run"));
	}

	public static class SubExampleTest extends ExampleTest {
	}
	
	@Test public void runWithExtendsToSubclasses() {
		log= "";

		JUnitCore.runClasses(SubExampleTest.class);
		assertTrue(log.contains("run"));
	}

	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(RunWithTest.class);
	}
}
