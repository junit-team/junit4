package org.junit.internal.sorters;

import java.lang.reflect.Method;

/**
 * Gets declared methods of a class in an unspecified, but predictable JVM specific order.
 */
public class JvmMethodSorter extends MethodSorter {

    /**
     * Using the JVM order is unwise since the Java platform does not
     * specify any particular order, and in fact JDK 7 returns a more or less
     * random order; well-written test code would not assume any order, but some
     * does, and a predictable failure is better than a random failure on
     * certain platforms. By default, uses an unspecified but deterministic order.
     *
     * @param clazz a class
     * @return same as {@link Class#getDeclaredMethods} but sorted
     * @see <a href="http://bugs.sun.com/view_bug.do?bug_id=7023180">JDK
     * (non-)bug #7023180</a>
     */
    @Override
    public Method[] getDeclaredMethods(Class<?> clazz) {
        return clazz.getDeclaredMethods();
    }
}
