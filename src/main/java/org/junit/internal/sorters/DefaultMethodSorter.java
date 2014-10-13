package org.junit.internal.sorters;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Gets declared methods of a class in a predictable order.
 */
public class DefaultMethodSorter extends ComparableMethodSorter {

    /**
     * DEFAULT sort order
     */
    public static final Comparator<Method> DEFAULT_COMPARATOR_INSTANCE = new DefaultMethodComparator();

    public DefaultMethodSorter() {
        super(DEFAULT_COMPARATOR_INSTANCE);
    }

    public static class DefaultMethodComparator implements Comparator<Method> {
        public int compare(Method m1, Method m2) {
            int i1 = m1.getName().hashCode();
            int i2 = m2.getName().hashCode();
            if (i1 != i2) {
                return i1 < i2 ? -1 : 1;
            }
            return NameAscendingMethodSorter.NAME_ASCENDING_COMPARATOR_INSTANCE.compare(m1, m2);
        }
    }
}
