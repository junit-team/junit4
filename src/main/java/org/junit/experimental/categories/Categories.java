/**
 * 
 */
package org.junit.experimental.categories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

/**
 * From a given set of test classes, runs only the classes and methods
 * that are annotated with either the category given with the @IncludeCategory
 * annotation, or a subtype of that category.
 * 
 * Example:
<pre>
	public interface FastTests extends CategoryType {}
	public interface SlowTests extends CategoryType {}

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

	@Category({SlowTests.class, FastTests.class})
	public static class B {
		@Test
		public void c() {

		}
	}

	@RunWith(Categories.class)
	@IncludeCategory(SlowTests.class)
	@SuiteClasses( { A.class, B.class }) // Note that Categories is a kind of Suite
	public static class SlowTestSuite {}
</pre>
 */
public class Categories extends Suite {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IncludeCategory {
		public Class<? extends CategoryType> value();
	}
	
	public static class CategoryFilter extends Filter {
		public static CategoryFilter include(
				Class<? extends CategoryType> categoryClass) {
			return new CategoryFilter(categoryClass);
		}

		private final Class<? extends CategoryType> fCategoryClass;

		public CategoryFilter(Class<? extends CategoryType> categoryClass) {
			fCategoryClass= categoryClass;
		}

		@Override
		public String describe() {
			return "category " + fCategoryClass;
		}

		@Override
		public boolean shouldRun(Description description) {
			if (hasCorrectCategoryAnnotation(description))
				return true;

			// TODO: feels as if we've done this child crawl several times.
			// Change design?
			for (Description each : description.getChildren())
				if (shouldRun(each))
					return true;
			return false;
		}

		private boolean hasCorrectCategoryAnnotation(Description description) {
			Category annotation= description.getAnnotation(Category.class);
			if (annotation == null)
				return false;
			for (Class<? extends CategoryType> each : annotation.value())
				if (fCategoryClass.isAssignableFrom(each))
					return true;
			return false;
		}
	}

	public Categories(Class<?> klass, RunnerBuilder builder)
			throws InitializationError {
		super(klass, builder);
		try {
			filter(new CategoryFilter(getCategory(klass)));
		} catch (NoTestsRemainException e) {
			throw new InitializationError(e);
		}
	}

	private Class<? extends CategoryType> getCategory(Class<?> klass) {
		return klass.getAnnotation(IncludeCategory.class).value();
	}
}