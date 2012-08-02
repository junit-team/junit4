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
	// the way filters are implemented makes this unnecessarily complicated,
	// buggy, and difficult to specify.  A new way of handling filters could
	// someday enable a better new implementation.
        // https://github.com/KentBeck/junit/issues/issue/172
	
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IncludeCategory {
		public Class<?>[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface ExcludeCategory {
		public Class<?>[] value();
	}

	public static class CategoryFilter extends Filter {
		public static CategoryFilter include(Class<?>... categoryType) {
			return new CategoryFilter(categoryType, null);
		}
		public static CategoryFilter include(Class<?> categoryType) {
			return new CategoryFilter(new Class<?>[] {categoryType}, null);
		}

		private final Class<?>[] fIncluded;

		private final Class<?>[] fExcluded;

		public CategoryFilter(Class<?>[] includedCategory,
				Class<?>[] excludedCategory) {
			fIncluded= includedCategory;
			fExcluded= excludedCategory;
		}

		@Override
		public String describe() {
			return ((fIncluded == null || fIncluded.length == 1) ? "category ":"categories ") + join(", ", fIncluded);
		}
		
		private String join(String seperator, Class<?>... values)
		{
			if(values == null || values.length == 0)
			{
				return "";
			}
			StringBuilder sb = new StringBuilder(values[0].toString());
			for(int i = 1; i < values.length; i++)
			{
				sb.append(seperator).append(values[i].toString());
			}
			return sb.toString();
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
			
			if(!methodContainsAnyExcludedCategories(categories, fExcluded))
			{
				return methodContainsAllIncludedCategories(categories, fIncluded);
			}
			return false;
		}

		private List<Class<?>> categories(Description description) {
			ArrayList<Class<?>> categories= new ArrayList<Class<?>>();
			categories.addAll(Arrays.asList(directCategories(description)));
			categories.addAll(Arrays.asList(directCategories(parentDescription(description))));
			return categories;
		}

		private Description parentDescription(Description description) {
			Class<?> testClass= description.getTestClass();
			if (testClass == null)
				return null;
			return Description.createSuiteDescription(testClass);
		}

		private Class<?>[] directCategories(Description description) {
			if (description == null)
				return new Class<?>[0];
			Category annotation= description.getAnnotation(Category.class);
			if (annotation == null)
				return new Class<?>[0];
			return annotation.value();
		}
		
		private boolean methodContainsAnyExcludedCategories(
				List<Class<?>> categories, Class<?>[] excludedCategories) {
			if (excludedCategories != null) {
				for (Class<?> eachExcluded : excludedCategories) {
					if (containsCategory(categories, eachExcluded)) {
						return true;
					}
				}
			}
			return false;
		}

		private boolean methodContainsAllIncludedCategories(
				List<Class<?>> categories, Class<?>[] includedCategories) {
			if (includedCategories != null) {
				for (Class<?> eachIncluded : includedCategories) {
					if (!containsCategory(categories, eachIncluded)) {
						return false;
					}
				}
			}
			return true;
		}

		private boolean containsCategory(List<Class<?>> categories,
				Class<?> categoryToMatch) {
			for (Class<?> each : categories) {
				if (categoryToMatch.isAssignableFrom(each)) {
					return true;
				}
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
		assertNoCategorizedDescendentsOfUncategorizeableParents(getDescription());
	}

	private Class<?>[] getIncludedCategory(Class<?> klass) {
		IncludeCategory annotation= klass.getAnnotation(IncludeCategory.class);
		return annotation == null ? null : annotation.value();
	}

	private Class<?>[] getExcludedCategory(Class<?> klass) {
		ExcludeCategory annotation= klass.getAnnotation(ExcludeCategory.class);
		return annotation == null ? null : annotation.value();
	}

	private void assertNoCategorizedDescendentsOfUncategorizeableParents(Description description) throws InitializationError {
		if (!canHaveCategorizedChildren(description))
			assertNoDescendantsHaveCategoryAnnotations(description);
		for (Description each : description.getChildren())
			assertNoCategorizedDescendentsOfUncategorizeableParents(each);
	}
	
	private void assertNoDescendantsHaveCategoryAnnotations(Description description) throws InitializationError {			
		for (Description each : description.getChildren()) {
			if (each.getAnnotation(Category.class) != null)
				throw new InitializationError("Category annotations on Parameterized classes are not supported on individual methods.");
			assertNoDescendantsHaveCategoryAnnotations(each);
		}
	}

	// If children have names like [0], our current magical category code can't determine their
	// parentage.
	private static boolean canHaveCategorizedChildren(Description description) {
		for (Description each : description.getChildren())
			if (each.getTestClass() == null)
				return false;
		return true;
	}
}