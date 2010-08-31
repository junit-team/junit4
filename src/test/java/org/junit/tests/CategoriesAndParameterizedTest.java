package org.junit.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import junit.framework.TestResult;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.categories.Category;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.tests.experimental.results.PrintableResultTest;

public class CategoriesAndParameterizedTest {
	public static class Token {

	}

	@RunWith(Parameterized.class)
	public static class ParameterizedTestA {
		public ParameterizedTestA(String a) {
		}

		@Parameters
		public static Collection<Object[]> getParameters() {
			return Collections.singletonList(new Object[] { "a" });
		}

		@Test
		public void testSomething() {
			Assert.assertTrue(true);
		}
	}

	@RunWith(Parameterized.class)
	public static class ParameterizedTestC {
		public ParameterizedTestC(String a) {
		}

		@Parameters
		public static Collection<Object[]> getParameters() {
			return Collections.singletonList(new Object[] { "a" });
		}

		@Test
		@Category(Token.class)
		public void testSomething() {
			Assert.assertTrue(true);
		}
	}

	@Category(Token.class)
	public static class SomeTestB {
		@Test
		public void testSomething() {
			Assert.assertTrue(true);
		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(Token.class)
	@SuiteClasses({ SomeTestB.class, ParameterizedTestA.class })
	public static class ParameterTokenSuite {
	}

	@RunWith(Categories.class)
	@IncludeCategory(Token.class)
	@SuiteClasses({ ParameterizedTestC.class, SomeTestB.class })
	public static class ParameterTokenSuiteC {
	}

	@Test
	public void shouldSucceedWithAParameterizedClassSomewhere() {
		Result result= new JUnitCore().run(ParameterTokenSuite.class);
		assertTrue(result.wasSuccessful());
	}

	@Ignore("Fix next time")
	@Test
	public void shouldFailWith() {
		Assert.assertThat(
				PrintableResult.testResult(ParameterTokenSuiteC.class),
				ResultMatchers
						.hasFailureContaining("Category annotations on Parameterized classes are not supported on individual methods."));
	}
}