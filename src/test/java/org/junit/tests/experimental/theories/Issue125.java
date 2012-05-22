package org.junit.tests.experimental.theories;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

/**
 * Test for Issue #125
 */
@RunWith(Theories.class)
public class Issue125 {

	int theInteger;

	@DataPoints
	public static Integer[] dataPoints1() {
		return new Integer[] {1,2};
	}

	// this is not static, so we should get a warning
	@DataPoints
	public Integer[] dataPoints2() {
		return new Integer[] {3,4};
	}

	public Issue125(int i) {
		theInteger = i;
	}

	@Test
	public void test() {
		System.out.println("Running test method for " + theInteger);
	}
}
