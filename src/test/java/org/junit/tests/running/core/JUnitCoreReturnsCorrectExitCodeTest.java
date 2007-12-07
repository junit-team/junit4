package org.junit.tests.running.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.tests.TestSystem;

public class JUnitCoreReturnsCorrectExitCodeTest {
	
	static public class Fail {
		@Test public void kaboom() {
			fail();
		}
	}
	
	@Test public void failureCausesExitCodeOf1() throws Exception {
		runClass(getClass().getName() + "$Fail", 1);
	}

	@Test public void missingClassCausesExitCodeOf1() throws Exception {
		runClass("Foo", 1);
	}

	static public class Succeed {
		@Test public void peacefulSilence() {
		}
	}
	
	@Test public void successCausesExitCodeOf0() throws Exception {
		runClass(getClass().getName() + "$Succeed", 0);
	}

	private void runClass(String className, int returnCode) {
		TestSystem system= new TestSystem();
		JUnitCore.runMainAndExit(system, className); 
		assertEquals(returnCode, system.fCode);
	}
}
