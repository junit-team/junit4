package org.junit.internal.sorters;

import java.lang.reflect.Method;

/**
 * Abstract class for all {@link org.junit.internal.sorters.MethodSorter}s.
 *
 * @see org.junit.SortWith
 */
public abstract class MethodSorter {

    /**
     * Gets declared methods of a class in a predictable order.
     *
     * @param clazz test class that you want to sort
     */
    public abstract Method[] getDeclaredMethods(Class<?> clazz);
}
