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
 * From a given set of test classes, runs only the classes and methods that are
 * annotated with either the category given with the @IncludeCategory
 * annotation, or a subtype of that category.
 * 
 * Example:
 * 
 * <pre>
 * public interface FastTests extends CategoryType {
 * }
 * 
 * public interface SlowTests extends CategoryType {
 * }
 * 
 * public static class A {
 * 	&#064;Test
 * 	public void a() {
 * 		fail();
 * 	}
 * 
 * 	&#064;Category(SlowTests.class)
 * 	&#064;Test
 * 	public void b() {
 * 	}
 * }
 * 
 * &#064;Category( { SlowTests.class, FastTests.class })
 * public static class B {
 * 	&#064;Test
 * 	public void c() {
 * 
 * 	}
 * }
 * 
 * &#064;RunWith(Categories.class)
 * &#064;IncludeCategory(SlowTests.class)
 * &#064;SuiteClasses( { A.class, B.class })
 * // Note that Categories is a kind of Suite
 * public static class SlowTestSuite {
 * }
 * </pre>
 */
public class Categories extends Suite {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IncludeCategory {
		public Class<?> value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ExcludeCategory {
		public Class<?> value();
	}

	public static class CategoryFilter extends Filter {
		public static CategoryFilter include(Class<?> categoryType) {
			return new CategoryFilter(categoryType, null);
		}

		private final Class<?> fIncluded;

		private final Class<?> fExcluded;

		public CategoryFilter(Class<?> includedCategory,
				Class<?> excludedCategory) {
			fIncluded= includedCategory;
			fExcluded= excludedCategory;
		}

		@Override
		public String describe() {
			return "category " + fIncluded;
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
				return fIncluded == null;
			for (Class<?> each : annotation.value()) {
				if (fExcluded != null && fExcluded.isAssignableFrom(each))
					return false;
				if (fIncluded == null || fIncluded.isAssignableFrom(each))
					return true;
			}
			return false;
		}
	}

	public Categories(Class<?> klass, RunnerBuilder builder)
			throws InitializationError {
		super(klass, builder);
		try {
			filter(new CategoryFilter(getIncludedCategory(klass),
					getExcludedCategory(klass)));
		} catch (NoTestsRemainException e) {
			throw new InitializationError(e);
		}
	}

	private Class<?> getIncludedCategory(Class<?> klass) {
		IncludeCategory annotation= klass.getAnnotation(IncludeCategory.class);
		return annotation == null ? null : annotation.value();
	}

	private Class<?> getExcludedCategory(Class<?> klass) {
		ExcludeCategory annotation= klass.getAnnotation(ExcludeCategory.class);
		return annotation == null ? null : annotation.value();
	}
}