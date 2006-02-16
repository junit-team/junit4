package org.junit.runner.manipulation;

import org.junit.runner.Description;
import org.junit.runner.Runner;

/**
 * The canonical case of filtering is when you want to run a single test method in a class. Rather
 * than introduce runner API just for that one case, JUnit provides a general filtering mechanism.
 * If you want to filter the tests to be run, extend <code>Filter</code> and apply an instance of
 * your filter to the <code>Request</code> before running it (see <code>JUnitCore.run(Request request)</code>). 
 * Alternatively, apply a <code>Filter</code> to a <code>Runner</code> before running
 * tests (for example, in conjunction with <code>@RunWith</code>.
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
	 * @return true if the test should be run
	 */
	public abstract boolean shouldRun(Description description);

	/**
	 * Invoke with a <code>Runner</code> to cause all tests it intends to run
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

	public abstract String describe();
}
