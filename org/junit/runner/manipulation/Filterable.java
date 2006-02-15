package org.junit.runner.manipulation;

public interface Filterable {

	void filter(Filter filter) throws NoTestsRemainException;

}
