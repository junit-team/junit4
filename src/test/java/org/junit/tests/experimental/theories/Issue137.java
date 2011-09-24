package org.junit.tests.experimental.theories;

import org.junit.Test;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

/**
 * Test for Issue #137
 */
@RunWith(Theories.class)
public class Issue137 {

	int theInteger;

	@DataPoints
	public static Integer[] dataPoints1() {
		return new Integer[] {1,2};
	}

	// this throws an error, so we should see that error when running tests
	@DataPoints
	public static Integer[] dataPoints2() {
		throw new RuntimeException("throwing exception from dataPoints2!");
	}

	public Issue137(int i) {
		theInteger = i;
	}

	@Test
	public void test() {
		System.out.println("Running test method for " + theInteger);
	}
}
