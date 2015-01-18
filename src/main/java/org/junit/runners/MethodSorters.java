package org.junit.runners;

import java.lang.reflect.Method;
import java.util.Comparator;

import org.junit.internal.MethodSorter;

/**
 * Sort the methods into a specified execution order.
 * Defines common {@link MethodSorter} implementations.
 *
 * @since 4.11
 */
public enum MethodSorters {
    /**
     * Sorts the test methods by the method name, in lexicographic order,
     * with {@link Method#toString()} used as a tiebreaker
     */
    NAME_ASCENDING(MethodSorter.NAME_ASCENDING),

    /**
     * Leaves the test methods in the order returned by the JVM.
     * Note that the order from the JVM may vary from run to run
     */
    JVM(null),

    /**
     * Sorts the test methods in a deterministic, but not predictable, order
     */
    DEFAULT(MethodSorter.DEFAULT);

    private final Comparator<Method> comparator;

    private MethodSorters(Comparator<Method> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Method> getComparator() {
        return comparator;
    }
}
