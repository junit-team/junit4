package org.junit.internal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class MethodSorter {

    /**
     * Gets declared methods of a class in a predictable order.
     * Using the "natural" order is unwise since the Java platform does not
     * specify any particular order, and in fact JDK 7 returns a more or less
     * random order; well-written test code would not assume any order, but some
     * does, and a predictable failure is better than a random failure on
     * certain platforms.
     * @param clazz a class
     * @return same as {@link Class#getDeclaredMethods} but sorted
     * @see <a href="http://bugs.sun.com/view_bug.do?bug_id=7023180">JDK
     *       (non-)bug #7023180</a>
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        Arrays.sort(methods, new Comparator<Method>() {
            @Override public int compare(Method m1, Method m2) {
                // Alpha sort by name, and secondarily by other differentiating
                // information (parameters and return type).
                return m1.toString().compareTo(m2.toString());
            }
        });
        return methods;
    }

    private MethodSorter() {}

}
