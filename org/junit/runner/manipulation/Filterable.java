package org.junit.runner.manipulation;

/**
 * Runners that allow filtering should implement this interface. Implement <code>filter()</code>
 * to remove tests that don't pass the filter.
 */
public interface Filterable {

	/**
	 * Remove tests that don't pass <code>filter</code>.
	 * @param filter the filter to apply
	 * @throws NoTestsRemainException if all tests are filtered out
	 */
	void filter(Filter filter) throws NoTestsRemainException;

}
