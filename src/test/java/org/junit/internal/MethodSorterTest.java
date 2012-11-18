package org.junit.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

public class MethodSorterTest {
    private static final String ALPHA = "java.lang.Object alpha(int,double,java.lang.Thread)";
    private static final String BETA = "void beta(int[][])";
    private static final String GAMMA_VOID = "int gamma()";
    private static final String GAMMA_BOOLEAN = "void gamma(boolean)";
    private static final String DELTA = "void delta()";
    private static final String EPSILON = "void epsilon()";
    private static final String SUPER_METHOD = "void superMario()";
    private static final String SUB_METHOD = "void subBowser()";

    static class DummySortWithoutAnnotation {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    static class Super {
        void superMario() {
        }
    }

    static class Sub extends Super {
        void subBowser() {
        }
    }

    private List<String> getDeclaredFilteredMethods(Class<?> clazz, List<String> ofInterest) {
        // the method under test.
        Method[] actualMethods = MethodSorter.getDeclaredMethods(clazz);

        // obtain just the names instead of the full methods.
        List<String> names = new ArrayList<String>();
        for (Method m : actualMethods) {
            names.add(m.toString().replace(clazz.getName() + '.', ""));
        }

        // filter to just the methods of interest
        names.retainAll(ofInterest);   	
        return names;
    }

    @Test
    public void testMethodsNullSorterSelf() {
        List<String> expected = Arrays.asList(
        		new String[]{EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN});
        List<String> actual = getDeclaredFilteredMethods(DummySortWithoutAnnotation.class, expected);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMethodsNullSorterSuper() {
        List<String> expected = Arrays.asList(new String[]{SUPER_METHOD});
        List<String> actual = getDeclaredFilteredMethods(Super.class, expected);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMethodsNullSorterSub() {
        List<String> expected = Arrays.asList(new String[]{SUB_METHOD});
        List<String> actual = getDeclaredFilteredMethods(Sub.class, expected);
        assertEquals(expected, actual);
    }

    @FixMethodOrder(MethodSorters.DEFAULT)
    static class DummySortWithDefault {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testDefaultMethodSorter() {
        List<String> expected = Arrays.asList(new String[]{EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN});
        List<String> actual = getDeclaredFilteredMethods(DummySortWithDefault.class, expected);
        assertEquals(expected, actual);
    }

    @FixMethodOrder(MethodSorters.JVM)
    static class DummySortJvm {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testJvmMethodSorter() {
        Method[] fromJvm = DummySortJvm.class.getDeclaredMethods();
        Method[] sorted = MethodSorter.getDeclaredMethods(DummySortJvm.class);
        assertArrayEquals(fromJvm, sorted);
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    static class DummySortWithNameAsc {
        Object alpha(int i, double d, Thread t) {
            return null;
        }

        void beta(int[][] x) {
        }

        int gamma() {
            return 0;
        }

        void gamma(boolean b) {
        }

        void delta() {
        }

        void epsilon() {
        }
    }

    @Test
    public void testAscendingMethodSorter() {
        List<String> expected = Arrays.asList(new String[]{ALPHA, BETA, DELTA, EPSILON, GAMMA_VOID, GAMMA_BOOLEAN});
        List<String> actual = getDeclaredFilteredMethods(DummySortWithNameAsc.class, expected);
        assertEquals(expected, actual);
    }
}
