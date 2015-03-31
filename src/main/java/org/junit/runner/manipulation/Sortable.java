package org.junit.runner.manipulation;

/**
 * Interface for runners that allow sorting of tests. By sorting tests based on when they last failed, most recently
 * failed first, you can reduce the average time to the first test failing. Test sorting should not be used to
 * cope with order dependencies between tests. Tests that are isolated from each other are less
 * expensive to maintain and can be run individually.
 *
 * @since 4.0
 */
public interface Sortable {

    /**
     * Sorts the tests using <code>sorter</code>
     *
     * @param sorter the {@link Sorter} to use for sorting the tests
     */
    void sort(Sorter sorter);

}
