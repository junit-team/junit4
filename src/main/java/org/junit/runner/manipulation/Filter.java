package org.junit.runner.manipulation;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Request;

/**
 * The canonical case of filtering is when you want to run a single test method in a class. Rather
 * than introduce runner API just for that one case, JUnit provides a general filtering mechanism.
 * If you want to filter the tests to be run, extend <code>Filter</code> and apply an instance of
 * your filter to the {@link org.junit.runner.Request} before running it (see 
 * {@link org.junit.runner.JUnitCore#run(Request)}. Alternatively, apply a <code>Filter</code> to 
 * a {@link org.junit.runner.Runner} before running tests (for example, in conjunction with 
 * {@link org.junit.runner.RunWith}.
 * @since 4.0
 */
public abstract class Filter {
	/**
	 * A null <code>Filter</code> that passes all tests through.
	 */
	public static Filter ALL= new Filter() {
		@Override
		public boolean shouldRun(Description description) {
			return true;
		}

		@Override
		public String describe() {
			return "all tests";
		}
		
		@Override
		public void apply(Object child) throws NoTestsRemainException {
			// do nothing
		}

		@Override
		public Filter intersect(Filter second) {
			return second;
		}
	};
	
	/**
	 * Returns a {@code Filter} that only runs the single method described by
	 * {@code desiredDescription}
	 */
	public static Filter matchMethodDescription(final Description desiredDescription) {
		return new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				if (description.isTest())
					return desiredDescription.equals(description);
				
				// explicitly check if any children want to run
				for (Description each : description.getChildren())
					if (shouldRun(each))
						return true;
				return false;					
			}

			@Override
			public String describe() {
				return String.format("Method %s", desiredDescription.getDisplayName());
			}
		};
	}

	/**
	 * If a filter is to be created from a list, there are two modes to choose from:
	 * <li> RELAXED: filter runs all methods in the list <b>and</b> all methods from classes not contained in the list. 
	 * <li> STRICT: filter runs only methods described in the list.
	 */
	public enum FilterMode { RELAXED, STRICT }
	
	/**
	 * Returns a {@link Filter} that runs methods described by {@code desiredDescriptions}.
	 * @param desiredDescriptions contains methods allowed by this filter
	 * @param mode if set to strict runs only methods in {@code desiredDescriptions}, 
	 * if set to relaxed runs methods in {@code desiredDescriptions} and methods from classes not in {@code desiredDescriptions}
	 * @return new {@link Filter} based on {@code desiredDescriptions}
	 */
	public static Filter matchMethodDescriptions(final List<Description> desiredDescriptions, final FilterMode mode) {
		final List<String> classNames= new ArrayList<String>();
		for (Description d : desiredDescriptions) {
			classNames.add(d.getClassName());
		}
		return new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				String methodName = description.getMethodName();
				String className = description.getClassName();
				if (methodName == null) {
					return true;
				}
				if (!classNames.contains(className) && mode.equals(FilterMode.RELAXED))
					return true;
				else if (!classNames.contains(className) && mode.equals(FilterMode.STRICT))
					return false;
				else
					return desiredDescriptions.contains(description);
			}
			@Override
			public String describe() {
				return "Description list method filter";
			}
		};
	}

	/**
	 * @param description the description of the test to be run
	 * @return <code>true</code> if the test should be run
	 */
	public abstract boolean shouldRun(Description description);

	/**
	 * Returns a textual description of this Filter
	 * @return a textual description of this Filter
	 */
	public abstract String describe();

	/**
	 * Invoke with a {@link org.junit.runner.Runner} to cause all tests it intends to run
	 * to first be checked with the filter. Only those that pass the filter will be run.
	 * @param child the runner to be filtered by the receiver
	 * @throws NoTestsRemainException if the receiver removes all tests
	 */
	public void apply(Object child) throws NoTestsRemainException {
		if (!(child instanceof Filterable))
			return;
		Filterable filterable= (Filterable) child;
		filterable.filter(this);
	}

	/**
	 * Returns a new Filter that accepts the intersection of the tests accepted
	 * by this Filter and {@code second}
	 */
	public Filter intersect(final Filter second) {
		if (second == this || second == ALL) {
			return this;
		}
		final Filter first= this;
		return new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				return first.shouldRun(description)
						&& second.shouldRun(description);
			}

			@Override
			public String describe() {
				return first.describe() + " and " + second.describe();
			}
		};
	}
}
