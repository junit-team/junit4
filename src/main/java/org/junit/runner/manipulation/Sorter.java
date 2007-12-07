package org.junit.runner.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;
import org.junit.runner.Runner;

/**
 * A <code>Sorter</code> orders tests. In general you will not need
 * to use a <code>Sorter</code> directly. Instead, use {@link org.junit.runner.Request#sortWith(Comparator)}.
 * 
 * 
 */
public class Sorter implements Comparator<Description> {
	public static void apply(Sorter sorter, Runner runner) {
		if (sorter != null)
			sorter.apply(runner);
	}
	
	private final Comparator<Description> fComparator;

	/**
	 * Creates a <code>Sorter</code> that uses <code>comparator</code>
	 * to sort tests
	 * @param comparator the {@link Comparator} to use when sorting tests
	 */
	public Sorter(Comparator<Description> comparator) {
		fComparator= comparator;
	}

	/**
	 * Sorts the test in <code>runner</code> using <code>comparator</code>
	 * @param runner
	 */
	public void apply(Runner runner) {
		if (runner instanceof Sortable) {
			Sortable sortable = (Sortable) runner;
			sortable.sort(this);
		}
	}

	/** @inheritDoc */
	public int compare(Description o1, Description o2) {
		return fComparator.compare(o1, o2);
	}
}
