package org.junit.internal.sorters;

import org.junit.SortWith;

import java.lang.reflect.Method;

/**
 * Utility class for constructing {@link org.junit.internal.sorters.MethodSorter} instance
 * from {@link org.junit.SortWith} annotation on a {@link org.junit.runners.model.TestClass}.
 *
 * It catches {@link org.junit.runners.model.InitializationError} and other reflection exceptions.
 *
 * If there is no {@link org.junit.SortWith} annotation specified, it uses
 * {@link org.junit.internal.sorters.DefaultMethodSorter} as a method sorter.
 */
public final class MethodSorterUtil {

    private MethodSorterUtil() {
    }

    public static Method[] getDeclaredMethods(Class<?> clazz) {
        MethodSorter methodSorter = null;
        try {
            SortWith annotation = clazz.getAnnotation(SortWith.class);
            if (annotation != null) {
                methodSorter = MethodSorterBuilder.buildMethodSorter(annotation.value());
            } else {
                methodSorter = new DefaultMethodSorter();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            methodSorter = new DefaultMethodSorter();
        }
        return methodSorter.getDeclaredMethods(clazz);
    }
}
