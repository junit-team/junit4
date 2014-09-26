package org.junit.internal;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Method;
import java.util.*;

public class MethodSorter {
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
    public static SortedMethods getDeclaredMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        FixMethodOrder annotation = clazz.getAnnotation(FixMethodOrder.class);

        if (annotation == null) {
            return sort(methods, MethodSorters.DEFAULT);
        }

        if (annotation.value() == MethodSorters.RANDOM) {
            return shuffle(methods, annotation.seed());
        }

        return sort(methods, annotation.value());
    }

    private MethodSorter() {
    }

    private static SortedMethods sort(Method[] methods, MethodSorters methodSorter) {
        Comparator<Method> comparator = methodSorter.getComparator();
        if (comparator != null) {
            Arrays.sort(methods, comparator);
        }

        return new SortedMethods(methods, methodSorter, null);
    }

    private static SortedMethods shuffle(Method[] methods, String seedText) {
        long seed = determineSeed(seedText);
        //TODO: how to display seed to user?
        //System.out.println("Methods shuffled with seed " + seed);
        Arrays.sort(methods, NAME_ASCENDING);
        List<Method> methodList = Arrays.asList(methods);
        Collections.shuffle(methodList, new Random(seed));

        return new SortedMethods(methodList.toArray(new Method[methodList.size()]), MethodSorters.RANDOM, seed);
    }

    private static long determineSeed(String seedText) {
        try {
            return Long.valueOf(seedText);
        } catch (NumberFormatException e) {
            //do nothing
        }
        return System.currentTimeMillis();
    }

    public static class SortedMethods {
        private Method[] methods;
        private MethodSorters methodSorter;
        private Long seed;

        public SortedMethods(Method[] methods, MethodSorters methodSorter, Long seed) {
            this.methods = methods;
            this.methodSorter = methodSorter;
            this.seed = seed;
        }

        public Method[] getMethods() {
            return methods;
        }

        public MethodSorters getMethodSorter() {
            return methodSorter;
        }

        public Long getSeed() {
            return seed;
        }
    }
}
