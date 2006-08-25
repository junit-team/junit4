package org.junit.runner.manipulation;

import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;

/**
 * The canonical case of filtering is when you want to run a single test method in a class. Rather
 * than introduce runner API just for that one case, JUnit provides a general filtering mechanism.
 * If you want to filter the tests to be run, extend <code>Filter</code> and apply an instance of
 * your filter to the {@link org.junit.runner.Request} before running it (see 
 * {@link org.junit.runner.JUnitCore#run(Request)}. Alternatively, apply a <code>Filter</code> to 
 * a {@link org.junit.runner.Runner} before running tests (for example, in conjunction with 
 * {@link org.junit.runner.RunWith}.
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
	};

	/**
	 * @param description the description of the test to be run
	 * @return <code>true</code> if the test should be run
	 */
	public abstract boolean shouldRun(Description description);

	/**
	 * Invoke with a {@link org.junit.runner.Runner} to cause all tests it intends to run
	 * to first be checked with the filter. Only those that pass the filter will be run.
	 * @param runner the runner to be filtered by the receiver
	 * @throws NoTestsRemainException if the receiver removes all tests
	 */
	public void apply(Runner runner) throws NoTestsRemainException {
		if (runner instanceof Filterable) {
			Filterable filterable= (Filterable)runner;
			filterable.filter(this);
		}
	}

	/**
	 * Returns a textual description of this Filter
	 * @return a textual description of this Filter
	 */
	public abstract String describe();
}
