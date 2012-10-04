package org.junit.internal;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;

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

    static class Dummy {
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
        void testOne() {
        }
    }

    static class Sub extends Super {
        void testTwo() {
        }
    }

    private String toString(Class<?> clazz, Method[] methods) {
        return Arrays.toString(methods).replace(clazz.getName() + '.', "");
    }

    private String declaredMethods(Class<?> clazz) {
        return toString(clazz, MethodSorter.getDeclaredMethods(clazz));
    }

    @Test
    public void getMethodsNullSorter() throws Exception {
        String[] expected = new String[]{EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN};
        assertEquals(Arrays.asList(expected).toString(), declaredMethods(Dummy.class));
        assertEquals("[void testOne()]", declaredMethods(Super.class));
        assertEquals("[void testTwo()]", declaredMethods(Sub.class));
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
    public void testDefaultSorter() {
        String[] expected = new String[]{EPSILON, BETA, ALPHA, DELTA, GAMMA_VOID, GAMMA_BOOLEAN};
        assertEquals(Arrays.asList(expected).toString(), declaredMethods(DummySortWithDefault.class));
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
    public void testSortWithJvm() {
        Class<?> clazz = DummySortJvm.class;
        String actual = toString(clazz, clazz.getDeclaredMethods());

        assertEquals(actual, declaredMethods(clazz));
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
    public void testNameAsc() {
        String[] expected = new String[]{ALPHA, BETA, DELTA, EPSILON, GAMMA_VOID, GAMMA_BOOLEAN};
        assertEquals(Arrays.asList(expected).toString(), declaredMethods(DummySortWithNameAsc.class));
    }
}
