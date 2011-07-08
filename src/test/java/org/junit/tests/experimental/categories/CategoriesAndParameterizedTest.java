package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
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

public class CategoriesAndParameterizedTest {
	public static class Token {

	}

	@RunWith(Parameterized.class)
	public static class WellBehavedParameterizedTest {
		public WellBehavedParameterizedTest(String a) {
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
	public static class ParameterizedTestWithAttemptedMethodCategory {
		public ParameterizedTestWithAttemptedMethodCategory(String a) {
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
	public static class VanillaCategorizedJUnitTest {
		@Test
		public void testSomething() {
			Assert.assertTrue(true);
		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(Token.class)
	@SuiteClasses({ VanillaCategorizedJUnitTest.class, WellBehavedParameterizedTest.class })
	public static class ParameterTokenSuiteWellFormed {
	}

	@RunWith(Categories.class)
	@IncludeCategory(Token.class)
	@SuiteClasses({ ParameterizedTestWithAttemptedMethodCategory.class, VanillaCategorizedJUnitTest.class })
	public static class ParameterTokenSuiteMalformed {
	}

	@RunWith(Categories.class)
	@IncludeCategory(Token.class)
	@SuiteClasses({ VanillaCategorizedJUnitTest.class, ParameterizedTestWithAttemptedMethodCategory.class })
	public static class ParameterTokenSuiteMalformedAndSwapped {
	}
   
	@Test
	public void shouldSucceedWithAParameterizedClassSomewhere() {
		Result result= new JUnitCore().run(ParameterTokenSuiteWellFormed.class);
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void shouldFailWithMethodLevelCategoryAnnotation() {
		Assert.assertThat(
				PrintableResult.testResult(ParameterTokenSuiteMalformed.class),
				ResultMatchers
						.hasFailureContaining("Category annotations on Parameterized classes are not supported on individual methods."));
	}

	@Test
	public void shouldFailWithMethodLevelCategoryAnnotationSwapped() {
		Assert.assertThat(
				PrintableResult.testResult(ParameterTokenSuiteMalformedAndSwapped.class),
				ResultMatchers
						.hasFailureContaining("Category annotations on Parameterized classes are not supported on individual methods."));
	}
}