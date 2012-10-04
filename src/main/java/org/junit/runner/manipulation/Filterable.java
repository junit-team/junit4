package org.junit.runner.manipulation;

/**
 * Runners that allow filtering should implement this interface. Implement {@link #filter(Filter)}
 * to remove tests that don't pass the filter.
 *
 * @since 4.0
 */
public interface Filterable {

    /**
     * Remove tests that don't pass the parameter <code>filter</code>.
     *
     * @param filter the {@link Filter} to apply
     * @throws NoTestsRemainException if all tests are filtered out
     */
    void filter(Filter filter) throws NoTestsRemainException;

}
