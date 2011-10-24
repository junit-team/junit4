package org.junit.tests.running.methods;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class NamedParameterizedTest {
	@RunWith(Parameterized.class)
	static public class NamedFoobar {
		@Parameters(fixtureDescription = "{0}[{1}] - ({3}, {2})", testDescription = "{0}[{1}]")
		public static List<Object[]> params() {
			return Arrays.asList(new Object[][]{
					{"foo", 11}, {"bar", 22}, {"baz", 33}
			});
		}

		private final String msg;
		public NamedFoobar(String s, int i) { msg = s + i; }
		@Test public void test1() {System.out.println("1:" + msg); }
		@Test public void test2() {System.out.println("2:" + msg);}
	}

	@Test public void testFoobar() throws Throwable {
		Parameterized foobar = new Parameterized(NamedFoobar.class);
		String className = NamedFoobar.class.getName();

		Description fixture = foobar.getDescription();
		assertEquals(className, fixture.getDisplayName());

		ArrayList<Description> parameterizedFixtures = fixture.getChildren();
		assertEquals(parameterizedFixtures.size(), 3);
		assertEquals(className + "[0] - 11, foo", parameterizedFixtures.get(0).getDisplayName());
		assertEquals(className + "[1] - 22, bar", parameterizedFixtures.get(1).getDisplayName());
		assertEquals(className + "[2] - 33, baz", parameterizedFixtures.get(2).getDisplayName());

		int i = 1;
		for (Description child : parameterizedFixtures) {
			ArrayList<Description> tests = child.getChildren();
			assertEquals(2, tests.size());
			assertEquals("test1[" + i + "](" + className + ")", tests.get(0).getDisplayName());
			assertEquals("test2[" + i + "](" + className + ")", tests.get(1).getDisplayName());
		}
	}
}