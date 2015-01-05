package org.junit.internal.sorters;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Gets declared methods of a class with a @{link Comparator} sort
 */
public class ComparableMethodSorter extends MethodSorter {

    private final Comparator<Method> comparator;

    public ComparableMethodSorter(Comparator<Method> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        if (comparator != null) {
            Arrays.sort(methods, comparator);
        }

        return methods;
    }
}
