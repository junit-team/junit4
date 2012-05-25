package org.junit.tests.experimental.theories;

import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class MethodDataPointsTest {

	static Set<String> foundStrings;
	static Set<Integer> foundInts;
	static Set<String> foundStringCombos;

	@BeforeClass
	public static void setupClass() {
		foundStrings = new HashSet<String>();
		foundInts = new HashSet<Integer>();
		foundStringCombos = new HashSet<String>();
	}

	@AfterClass
	public static void teardownClass() {
		assertThat(foundStrings.size(), equalTo(3));
		assertThat(foundInts.size(), equalTo(4));
		assertThat(foundStrings, hasItems("one", "two", "three"));
		assertThat(foundInts, hasItems(1, 2, 3, 4));

		assertThat(foundStringCombos.size(), equalTo(12));
		for (int i = 1 ; i <= 4; i++) {
			assertThat(foundStringCombos, hasItems("one-" + i, "two-" + i, "three-" + i));
		}
	}

	@DataPoints
	public static String[] getStrings() {
		return new String[] {"one", "two"};
	}

	@DataPoints
	public static int[] getInts() {
		return new int[] {1,2, 3};
	}

	@DataPoint
	public static String getString() {
		return "three";
	}

	@DataPoint
	public static int getInt() {
		return 4;
	}

	@Theory
	public void testCombo(String str, int id) {
		foundStrings.add(str);
		foundInts.add(id);
		foundStringCombos.add(str + "-" + id);
	}

}
