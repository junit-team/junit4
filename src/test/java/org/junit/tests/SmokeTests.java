package org.junit.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.runner.notification.RunNotifierTest;

/**
 * Some tests to verify that notifications of failing tests works.
 * These tests are JUnit3-style tests so ensure that bugs
 * in the core code are caught.
 *
 * @author kcooney (Kevin Cooney)
 */
public class SmokeTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Smoke Tests");
		suite.addTestSuite(RunNotifierTest.class);
		return suite;
	}
}
