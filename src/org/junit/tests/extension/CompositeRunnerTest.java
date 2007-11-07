package org.junit.tests.extension;

import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.CompositeRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

public class CompositeRunnerTest {
	public static class CompositeWithClass extends CompositeRunner {
		public CompositeWithClass(Class<?> type) {
			super(type);
		}
	}
	
	@RunWith(CompositeWithClass.class) public static class OnlyBeforeClass {
		public static boolean beforeClassRun = false;
		
		@BeforeClass public static void beforeClass() {
			beforeClassRun = true;
		}
	}
	
	@Test public void callsBeforeClass() {
		JUnitCore.runClasses(OnlyBeforeClass.class);
		assertTrue(OnlyBeforeClass.beforeClassRun);
	}
}
