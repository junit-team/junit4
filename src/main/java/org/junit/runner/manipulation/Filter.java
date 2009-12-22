package org.junit.runner.manipulation;

import org.junit.runner.Description;
import org.junit.runner.Request;

/**
 * The canonical case of filtering is when you want to run a single test method
 * in a class. Rather than introduce runner API just for that one case, JUnit
 * provides a general filtering mechanism.
 * 
 * If you want to filter the tests to be run, extend <code>Filter</code> and
 * apply an instance of your filter to the {@link org.junit.runner.Request}
 * before running it (see {@link org.junit.runner.JUnitCore#run(Request)}.
 * 
 * Alternatively, apply a <code>Filter</code> to a
 * {@link org.junit.runner.Runner} before running tests (for example, in
 * conjunction with {@link org.junit.runner.RunWith}.
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
	 * Returns a {@code Filter} that only runs the single method described by
	 * {@code desiredDescription}
	 */
	public static Filter matchMethodDescription(final Description desiredDescription) {
		return new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				if (description.isTest())
					return desiredDescription.equals(description);
				return true;
			}

			@Override
			public String describe() {
				return String.format("Method %s", desiredDescription.getDisplayName());
			}
		};
	}

	/**
	 * Indicates how to prune the tree of tests. The meaning of the boolean
	 * returned depends on whether {@code description} describes a leaf in the
	 * test tree ({@code Description.isTest} is true), or a suite ({@code
	 * Description.isSuite} is true).
	 * 
	 * If {@code description} is a leaf, then {@code shouldRun} returns false
	 * iff the test represented should not be run.
	 * 
	 * If {@code description} is a suite, then {@code shouldRun} may return
	 * false if it is cheap to determine that no leaf recursively included by
	 * this suite should be run. However, {@code shouldRun} may return true for
	 * a suite, and yet return false for every member of that suite, in which
	 * case the suite should throw {@link NoTestsRemainException} from the
	 * {@code filter} method.
	 *
	 * I apologize for any inconvenience caused by this specification. 
	 * 
	 * @param description
	 *            the description of the test to be run
	 * @return <code>true</code> if the test should be run.
	 * @author saff
	 */
	public abstract boolean shouldRun(Description description);

	/**
	 * Returns a textual description of this Filter
	 * 
	 * @return a textual description of this Filter
	 */
	public abstract String describe();

	/**
	 * Invoke with a {@link org.junit.runner.Runner} to cause all tests it
	 * intends to run to first be checked with the filter. Only those that pass
	 * the filter will be run.
	 * 
	 * @param child
	 *            the runner to be filtered by the receiver
	 * @throws NoTestsRemainException
	 *             if the receiver removes all tests
	 */
	public void apply(Object child) throws NoTestsRemainException {
		if (!(child instanceof Filterable))
			return;
		Filterable filterable= (Filterable) child;
		filterable.filter(this);
	}
}
