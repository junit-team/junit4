package org.junit.runners;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.junit.internal.MethodSorter;

/**
 * Sort the methods into a specified execution order
 */
public enum MethodSorters {
	/** Name ascending */
	NAME_ASC(MethodSorter.NAME_ASC),
	/** Name descending */
	NAME_DESC(MethodSorter.NAME_DESC),
	/** default JVM, (no sort) */
	JVM(null),
	/** Default, deterministic but not predictable */
	DEFAULT(MethodSorter.DEFAULT);
	
	private final Comparator<Method> fComparator;

	private MethodSorters(Comparator<Method> comparator) {
		this.fComparator= comparator;
	}

	public Comparator<Method> getComparator() {
		return fComparator;
	}
}
