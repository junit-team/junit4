package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.tests.experimental.categories.CategoryTest.CategoryRunner.IncludeCategory;

public class CategoryTest {
	public static class CategoryRunner extends Suite {
		@Retention(RetentionPolicy.RUNTIME)
		public @interface IncludeCategory {
			public Class<? extends CategoryClass> value();
		}

		public CategoryRunner(Class<?> klass, RunnerBuilder builder)
				throws InitializationError {
			super(klass, builder);
			try {
				filter(new CategoryFilter(getCategory(klass)));
			} catch (NoTestsRemainException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// TODO: figure out what should happen if everything is filtered out, and test that it does.
			}
		}

		private Class<? extends CategoryClass> getCategory(Class<?> klass) {
			return klass.getAnnotation(IncludeCategory.class).value();
		}
	}

	public static interface FastTests extends CategoryClass {

	}

	@RunWith(CategoryRunner.class)
	@IncludeCategory(FastTests.class)
	@SuiteClasses( { A.class })
	public static class FilterMeFast {

	}

	@Test
	public void getCategory() throws InitializationError {
		assertEquals(FastTests.class, new CategoryRunner(FilterMeFast.class,
				new AllDefaultPossibilitiesBuilder(true))
				.getCategory(FilterMeFast.class));
	}

	// TODO: move
	public static class CategoryFilter extends Filter {
		public static CategoryFilter include(
				Class<? extends CategoryClass> categoryClass) {
			return new CategoryFilter(categoryClass);
		}

		private final Class<? extends CategoryClass> fCategoryClass;

		public CategoryFilter(Class<? extends CategoryClass> categoryClass) {
			fCategoryClass= categoryClass;
		}

		@Override
		public String describe() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean shouldRun(Description description) {
			// TODO: ack! why is value single-valued?
			// TODO: not just any category should work
			// TODO: should inheritance come into play?
			Category annotation= description.getAnnotation(Category.class);
			return annotation != null && annotation.value() == fCategoryClass;
		}
	}

	// TODO: an interface that's called a class?
	public interface CategoryClass {

	}

	public interface SlowTests extends CategoryClass {

	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Category {
		Class<? extends CategoryClass> value();
	}

	public static class A {
		@Test
		public void a() {
			fail();
		}

		@Category(SlowTests.class)
		@Test
		public void b() {
		}
	}

	@Category(SlowTests.class)
	public static class B {
		@Test
		public void c() {

		}
	}

	public static class C {
		@Test
		public void d() {
			fail();
		}
	}

	@RunWith(CategoryRunner.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { A.class })
	public static class JustA {
	}

	@Test
	public void testCountOnJustA() {
		assertThat(testResult(JustA.class), isSuccessful());
	}

	@RunWith(CategoryRunner.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { A.class, B.class, C.class })
	public static class SlowTestSuite {
	}

	@Test
	public void testCount() {
		assertThat(testResult(SlowTestSuite.class), isSuccessful());
	}

	@RunWith(Suite.class)
	@SuiteClasses( { A.class, B.class, C.class })
	public static class TestSuiteWithNoCategories {
	}

	@Test
	public void testCountWithExplicitFilter() throws Throwable {
		CategoryFilter include= CategoryFilter.include(SlowTests.class);
		Request baseRequest= Request.aClass(TestSuiteWithNoCategories.class);
		Result result= new JUnitCore().run(baseRequest.filterWith(include));
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void categoryFilterLeavesOnlyMatchingMethods()
			throws InitializationError, NoTestsRemainException {
		CategoryFilter filter= CategoryFilter.include(SlowTests.class);
		BlockJUnit4ClassRunner runner= new BlockJUnit4ClassRunner(A.class);
		filter.apply(runner);
		assertEquals(1, runner.testCount());
	}

	@Test
	public void categoryFilterMatchesAnnotatedMethod() {
		CategoryFilter filter= CategoryFilter.include(SlowTests.class);
		assertTrue(filter.shouldRun(Description.createTestDescription(A.class,
				"b", new Category() {
					public Class<? extends CategoryClass> value() {
						// TODO Auto-generated method stub
						return SlowTests.class;
					}

					public Class<? extends Annotation> annotationType() {
						return Category.class;
					}
				})));
	}

	public static class AllFastTests {
		@Category(FastTests.class)
		@Test
		public void a() {

		}

		@Category(SlowTests.class)
		@Test
		public void b() {

		}
	}

	// TODO: What to do when a class has nothing in the category

	@Test
	public void categoryFilterRejectsIncompatibleCategory()
			throws InitializationError, NoTestsRemainException {
		CategoryFilter filter= CategoryFilter.include(SlowTests.class);
		BlockJUnit4ClassRunner runner= new BlockJUnit4ClassRunner(
				AllFastTests.class);
		filter.apply(runner);
		assertEquals(1, runner.testCount());
	}
}
