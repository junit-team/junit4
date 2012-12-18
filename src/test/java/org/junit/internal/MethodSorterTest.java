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

    private List<String> getDeclaredMethodNames(Class<?> clazz) {
        Method[] actualMethods = MethodSorter.getDeclaredMethods(clazz);

        // Obtain just the names instead of the full methods.
        List<String> names = new ArrayList<String>();
        for (Method m : actualMethods) {
            // Filter out synthetic methods from, e.g., coverage tools.
            if (!m.isSynthetic()) {
                names.add(m.toString().replace(clazz.getName() + '.', ""));
        	}
        }
        
        return names;
    }

    @Test
    public void testMethodsNullSorterSelf() {
        List<String> expected = Arrays.asList(EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(DummySortWithoutAnnotation.class);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMethodsNullSorterSuper() {
        List<String> expected = Arrays.asList(SUPER_METHOD);
        List<String> actual = getDeclaredMethodNames(Super.class);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testMethodsNullSorterSub() {
        List<String> expected = Arrays.asList(SUB_METHOD);
        List<String> actual = getDeclaredMethodNames(Sub.class);
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
        List<String> expected = Arrays.asList(EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(DummySortWithDefault.class);
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
        Method[] fromJvmWithSynthetics = DummySortJvm.class.getDeclaredMethods();
        Method[] sorted = MethodSorter.getDeclaredMethods(DummySortJvm.class);
        assertArrayEquals(fromJvmWithSynthetics, sorted);
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
        List<String> expected = Arrays.asList(ALPHA, BETA, DELTA, EPSILON, GAMMA_VOID, GAMMA_BOOLEAN);
        List<String> actual = getDeclaredMethodNames(DummySortWithNameAsc.class);
        assertEquals(expected, actual);
    }
}
