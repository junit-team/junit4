package org.junit.internal;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import org.junit.FixMethodOrder;

public class MethodSorter {
    /**
     * DEFAULT sort order
     */
    public static Comparator<Method> DEFAULT= new Comparator<Method>() {
        public int compare(Method m1, Method m2) {
            int i1 = m1.getName().hashCode();
            int i2 = m2.getName().hashCode();
            return i1 != i2 ? i1 - i2 : MethodSorter.compare(m1.toString(), m2.toString());
        }
    };
    
    /**
     * Method name ascending lexicograhic sort order
     */
    public static Comparator<Method> NAME_ASCENDING= new Comparator<Method>() {
        public int compare(Method m1, Method m2) {
            return MethodSorter.compare(m1.getName(), m2.getName());
        }
    };

    private static int compare(String s1, String s2) {
        return s1.compareTo(s2);
    }
    
    /**
     * Gets declared methods of a class in a predictable order, unless @FixMethodOrder(MethodSorters.JVM) is specified.
     * 
     * Using the JVM order is unwise since the Java platform does not
     * specify any particular order, and in fact JDK 7 returns a more or less
     * random order; well-written test code would not assume any order, but some
     * does, and a predictable failure is better than a random failure on
     * certain platforms. By default, uses an unspecified but deterministic order.
     * @param clazz a class
     * @return same as {@link Class#getDeclaredMethods} but sorted
     * @see <a href="http://bugs.sun.com/view_bug.do?bug_id=7023180">JDK
     *       (non-)bug #7023180</a>
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        Comparator<Method> comparator= getSorter(clazz.getAnnotation(FixMethodOrder.class));
        
        Method[] methods= clazz.getDeclaredMethods();
        if (comparator != null) {
            Arrays.sort(methods, comparator);
        }

        return methods;
    }

    private MethodSorter() {}

    private static Comparator<Method> getSorter(FixMethodOrder fixMethodOrder) {
        if (fixMethodOrder == null) {
            return DEFAULT;
        }

        return fixMethodOrder.value().getComparator();
    }
}
