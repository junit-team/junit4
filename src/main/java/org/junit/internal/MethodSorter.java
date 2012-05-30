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
        public int compare(Method left, Method right) {
            if (MethodSorter.hashCodesAreEqual(left, right)) {
                return MethodSorter.compareSignatures(left, right);
            }

            return MethodSorter.compareHashCodes(left, right);
        }
    };
    
    /**
     * Method name ascending lexicograhic sort order
     */
    public static Comparator<Method> NAME_ASCENDING= new Comparator<Method>() {
        public int compare(Method left, Method right) {
            if (MethodSorter.namesAreEqual(left, right)) {
              return MethodSorter.compareSignatures(left, right);
            }

            return MethodSorter.compareNames(left, right);
        }
    };

    private static int compareHashCodes(Method left, Method right) {
        return left.getName().hashCode() <= right.getName().hashCode() ? -1 : 1;
    }

    private static int compareNames(Method left, Method right) {
        return left.getName().compareTo(right.getName());
    }

    private static int compareSignatures(Method left, Method right) {
        return left.toString().compareTo(right.toString());
    }

    private static boolean hashCodesAreEqual(Method left, Method right) {
        return left.getName().hashCode() == right.getName().hashCode();
    }

    private static boolean namesAreEqual(Method left, Method right) {
        return left.getName().equals(right.getName());
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
