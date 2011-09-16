package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.experimental.categories.Category;
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

	@RunWith(Parameterized.class)
	@Category(Token.class)
	public static class ParameterizedTestWithClassCategory {
		public ParameterizedTestWithClassCategory(String a) {
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

	@Category(Token.class)
	public static class VanillaCategorizedJUnitTest {
		@Test
		public void testSomething() {
			Assert.assertTrue(true);
		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(Token.class)
	@SuiteClasses({ VanillaCategorizedJUnitTest.class, 
					WellBehavedParameterizedTest.class,
					ParameterizedTestWithClassCategory.class })
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
		assertThat(testResult(ParameterTokenSuiteWellFormed.class),
				isSuccessful());
	}

	@Test
	public void shouldFailWithMethodLevelCategoryAnnotation() {
		assertThat(
				testResult(ParameterTokenSuiteMalformed.class),
				hasFailureContaining("Category annotations on Parameterized classes are not supported on individual methods."));
	}

	@Test
	public void shouldFailWithMethodLevelCategoryAnnotationSwapped() {
		assertThat(
				testResult(ParameterTokenSuiteMalformedAndSwapped.class),
				hasFailureContaining("Category annotations on Parameterized classes are not supported on individual methods."));
	}
}