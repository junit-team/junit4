package org.junit.runner.manipulation;

import org.junit.runner.Category;
import org.junit.runner.Description;

public class CategoryFilter extends Filter {

	private final Class<?> fCategory;

	public CategoryFilter(Class<?> category) {
		fCategory= category;
	}

	@Override
	public String describe() {
		return "in category " + fCategory.getSimpleName();
	}

	@Override
	public boolean shouldRun(Description description) {
		Category annotation= description.getAnnotation(Category.class);
		if (annotation != null)
			return isCorrectCategory(annotation);
		Category parentAnnotation = description.getParentAnnotation(Category.class);
		return (parentAnnotation != null) && isCorrectCategory(parentAnnotation);
	}

	private boolean isCorrectCategory(Category parentAnnotation) {
		return parentAnnotation.value().equals(fCategory);
	}
}
