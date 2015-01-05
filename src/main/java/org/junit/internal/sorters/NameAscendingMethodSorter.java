package org.junit.internal.sorters;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Gets declared methods of a class in a method name ascending order.
 */
public class NameAscendingMethodSorter extends ComparableMethodSorter {

    /**
     * Method name ascending lexicographic sort order, with {@link java.lang.reflect.Method#toString()} as a tiebreaker
     */
    public static final Comparator<Method> NAME_ASCENDING_COMPARATOR_INSTANCE = new NameAscendingMethodComparator();

    public NameAscendingMethodSorter() {
        super(NAME_ASCENDING_COMPARATOR_INSTANCE);
    }

    private static class NameAscendingMethodComparator implements Comparator<Method> {
        public int compare(Method m1, Method m2) {
            final int comparison = m1.getName().compareTo(m2.getName());
            if (comparison != 0) {
                return comparison;
            }
            return m1.toString().compareTo(m2.toString());
        }
    }
}
