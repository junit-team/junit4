package org.junit.internal;

import org.junit.FixMethodOrder;
import org.junit.Seed;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MethodSorter {

    /* 
     * Used for java.util.Random seed generation.
     * This is the same magic number used in the Java API
     * for java.util.Random.
     */
    private static volatile long seedUniquifier = 8682522807148012L;

    /**
     * DEFAULT sort order
     */
    public static final Comparator<Method> DEFAULT = new Comparator<Method>() {
        public int compare(Method m1, Method m2) {
            int i1 = m1.getName().hashCode();
            int i2 = m2.getName().hashCode();
            if (i1 != i2) {
                return i1 < i2 ? -1 : 1;
            }
            return NAME_ASCENDING.compare(m1, m2);
        }
    };

    /**
     * Method name ascending lexicographic sort order, with {@link Method#toString()} as a tiebreaker
     */
    public static final Comparator<Method> NAME_ASCENDING = new Comparator<Method>() {
        public int compare(Method m1, Method m2) {
            final int comparison = m1.getName().compareTo(m2.getName());
            if (comparison != 0) {
                return comparison;
            }
            return m1.toString().compareTo(m2.toString());
        }
    };

    /**
     * Gets declared methods of a class in a predictable order, unless @FixMethodOrder(MethodSorters.JVM) is specified.
     *
     * Using the JVM order is unwise since the Java platform does not
     * specify any particular order, and in fact JDK 7 returns a more or less
     * random order; well-written test code would not assume any order, but some
     * does, and a predictable failure is better than a random failure on
     * certain platforms. By default, uses an unspecified but deterministic order.
     *
     * @param clazz a class
     * @return same as {@link Class#getDeclaredMethods} but sorted
     * @see <a href="http://bugs.sun.com/view_bug.do?bug_id=7023180">JDK
     *      (non-)bug #7023180</a>
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        FixMethodOrder order = clazz.getAnnotation(FixMethodOrder.class);
        Comparator<Method> comparator = getSorter(order);

        Method[] methods = clazz.getDeclaredMethods();
        if (comparator != null) {
            Arrays.sort(methods, comparator);
        }
        if(order != null && order.value() == MethodSorters.RANDOM) {
            Seed s = clazz.getAnnotation(Seed.class);
            long seed = s == null ? generateSeed() : s.value();
            System.out.printf("Class: %s, Random ordering seed = 0x%xL%n", clazz.getName(), seed);
            Random r = new Random(seed);
            Collections.shuffle(Arrays.asList(methods), r);
        }

        return methods;
    }

    // Same seed generation algorithm used in JDK 6
    private static long generateSeed() {
        return ++seedUniquifier + System.nanoTime();
    }


    private MethodSorter() {
    }

    private static Comparator<Method> getSorter(FixMethodOrder fixMethodOrder) {
        if (fixMethodOrder == null) {
            return DEFAULT;
        }

        return fixMethodOrder.value().getComparator();
    }
}
