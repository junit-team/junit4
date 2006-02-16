package org.junit.runner.manipulation;

import java.util.Comparator;

import org.junit.runner.Description;
import org.junit.runner.Runner;

//TODO add an example
/**
 * A <code>Sorter</code> orders tests. In general you will not need
 * to use a <code>Sorter</code> directly. Instead, use <code>Request.sortWith(Comparator<Description>)</code>.
 */
public class Sorter implements Comparator<Description> {
	private final Comparator<Description> fComparator;

	public Sorter(Comparator<Description> comparator) {
		fComparator= comparator;
	}

	public void apply(Runner runner) {
		if (runner instanceof Sortable) {
			Sortable sortable= (Sortable) runner;
			sortable.sort(this);
		}
	}

	public int compare(Description o1, Description o2) {
		return fComparator.compare(o1, o2);
	}
}
