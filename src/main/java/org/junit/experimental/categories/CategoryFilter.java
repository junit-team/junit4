/**
 * 
 */
package org.junit.experimental.categories;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.experimental.runners.SuiteBuilder;
import org.junit.runner.Runner;

public class CategoryFilter implements SuiteBuilder.RunnerFilter.Value {
	private final Class<?> fIncluded;

	public CategoryFilter(Class<?> included) {
		fIncluded= included;
	}

	public static CategoryFilter include(Class<?> included) {
		return new CategoryFilter(included);
	}

	public List<Runner> matchingRunners(List<Runner> allPossibleRunners) {
		ArrayList<Runner> result= new ArrayList<Runner>();
		for (Runner each : allPossibleRunners)
			if (shouldInclude(each))
				result.add(each);
		return result;
	}

	private boolean shouldInclude(Runner r) {
		for (Annotation each : r.getDescription().getAnnotations())
			if (isCategory(each) && categoryIsIncluded((Category) each))
				return true;
		return false;
	}

	private boolean isCategory(Annotation a) {
		return a.annotationType().equals(Category.class);
	}

	private boolean categoryIsIncluded(Category a) {
		return Arrays.asList(a.value()).contains(fIncluded);
	}
}