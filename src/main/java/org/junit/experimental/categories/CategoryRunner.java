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

public class CategoryRunner extends Suite {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface IncludeCategory {
		public Class<? extends CategoryClass> value();
	}
	
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
			for (Class<? extends CategoryClass> each : annotation.value())
				if (fCategoryClass.isAssignableFrom(each))
					return true;
			return false;
		}
	}

	public CategoryRunner(Class<?> klass, RunnerBuilder builder)
			throws InitializationError {
		super(klass, builder);
		try {
			filter(new CategoryFilter(getCategory(klass)));
		} catch (NoTestsRemainException e) {
			throw new InitializationError(e);
		}
	}

	private Class<? extends CategoryClass> getCategory(Class<?> klass) {
		return klass.getAnnotation(IncludeCategory.class).value();
	}
}