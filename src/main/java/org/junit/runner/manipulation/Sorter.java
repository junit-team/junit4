package org.junit.runner.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;

/**
 * A <code>Sorter</code> orders tests. In general you will not need
 * to use a <code>Sorter</code> directly. Instead, use {@link org.junit.runner.Request#sortWith(Comparator)}.
 * 
 * 
 */
public class Sorter implements Comparator<Description> {
	/**
	 * NULL is a <code>Sorter</code> that leaves elements in an undefined order
	 */
	public static Sorter NULL= new Sorter(new Comparator<Description>() {
		public int compare(Description o1, Description o2) {
			return 0;
		}});
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
	 * @param object
	 */
	public void apply(Object object) {
		if (object instanceof Sortable) {
			Sortable sortable = (Sortable) object;
			sortable.sort(this);
		}
	}

	public int compare(Description o1, Description o2) {
		return fComparator.compare(o1, o2);
	}
}
