/**
 * 
 */
package org.junit.experimental.categories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * Note that, for now, annotating suites with {@code @Category} has no effect.
 * Categories must be annotated on the direct method or class.
 * 
 * Example:
 * 
 * <pre>
 * public interface FastTests {
 * }
 * 	
 * public interface SlowTests {
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
			for (Description each : description.getChildren())
				if (shouldRun(each))
					return true;
			return false;
		}

		private boolean hasCorrectCategoryAnnotation(Description description) {
			List<Class<?>> categories= categories(description);
			if (categories.isEmpty())
				return fIncluded == null;
			for (Class<?> each : categories)
				if (fExcluded != null && fExcluded.isAssignableFrom(each))
					return false;
			for (Class<?> each : categories)
				if (fIncluded == null || fIncluded.isAssignableFrom(each))
					return true;
			return false;
		}

		private List<Class<?>> categories(Description description) {
			ArrayList<Class<?>> categories= new ArrayList<Class<?>>();
			categories.addAll(Arrays.asList(directCategories(description)));
			categories.addAll(Arrays.asList(directCategories(parentDescription(description))));
			return categories;
		}

		private Description parentDescription(Description description) {
			// TODO: how heavy are we cringing?
			return Description.createSuiteDescription(description.getTestClass());
		}

		private Class<?>[] directCategories(Description description) {
			Category annotation= description.getAnnotation(Category.class);
			if (annotation == null)
				return new Class<?>[0];
			return annotation.value();
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