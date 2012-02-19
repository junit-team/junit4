package org.junit.internal;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.SortMethodsWith;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.*;

public class MethodSorterTest {
    private static class Dummy {
        Object alpha(int i, double d, Thread t) {return null;}
        void beta(int[][] x) {}
        int gamma() {return 0;}
        void gamma(boolean b) {}
        void delta() {}
        void epsilon() {}
    }
    private static class Super {
        void testOne() {}
    }
    private static class Sub extends Super {
        void testTwo() {}
    }
    
    private String toString(Class<?> clazz, Method[] methods) {
    	return Arrays.toString(methods).replace(clazz.getName() + '.', "");
    }
    
    private String declaredMethods(Class<?> clazz) {
        return toString(clazz, MethodSorter.getDeclaredMethods(clazz));
    }

    @Test public void getMethodsNullSorter() throws Exception {
        assertEquals("[void epsilon(), void beta(int[][]), java.lang.Object alpha(int,double,java.lang.Thread), void delta(), int gamma(), void gamma(boolean)]", declaredMethods(Dummy.class));
        assertEquals("[void testOne()]", declaredMethods(Super.class));
        assertEquals("[void testTwo()]", declaredMethods(Sub.class));
    }
	
    @SortMethodsWith(MethodSorters.DEFAULT)
    private static class DummySortWithDefault {
        Object alpha(int i, double d, Thread t) {return null;}
        void beta(int[][] x) {}
        int gamma() {return 0;}
        void gamma(boolean b) {}
        void delta() {}
        void epsilon() {}
    }

	@Test public void testDefaultSorter() {
        assertEquals("[void epsilon(), void beta(int[][]), java.lang.Object alpha(int,double,java.lang.Thread), void delta(), int gamma(), void gamma(boolean)]", declaredMethods(DummySortWithDefault.class));
	}
	
    @SortMethodsWith(MethodSorters.JVM)
    private static class DummySortJvm {
        Object alpha(int i, double d, Thread t) {return null;}
        void beta(int[][] x) {}
        int gamma() {return 0;}
        void gamma(boolean b) {}
        void delta() {}
        void epsilon() {}
    }

	@Test public void testSortWithJvm() {
		Class<?> clazz = DummySortJvm.class;
        String actual = toString(clazz, clazz.getDeclaredMethods());

        assertEquals(actual, declaredMethods(clazz));
	}

    @SortMethodsWith(MethodSorters.NAME_ASC)
    private static class DummySortWithNameAsc {
        Object alpha(int i, double d, Thread t) {return null;}
        void beta(int[][] x) {}
        int gamma() {return 0;}
        void gamma(boolean b) {}
        void delta() {}
        void epsilon() {}
    }

	@Test public void testNameAsc() {
        assertEquals("[java.lang.Object alpha(int,double,java.lang.Thread), void beta(int[][]), void delta(), void epsilon(), int gamma(), void gamma(boolean)]", declaredMethods(DummySortWithNameAsc.class));
	}

    @SortMethodsWith(MethodSorters.NAME_DESC)
    private static class DummySortWithNameDesc {
        Object alpha(int i, double d, Thread t) {return null;}
        void beta(int[][] x) {}
        int gamma() {return 0;}
        void gamma(boolean b) {}
        void delta() {}
        void epsilon() {}
    }

	@Test public void testNameDesc() {
        assertEquals("[int gamma(), void gamma(boolean), void epsilon(), void delta(), void beta(int[][]), java.lang.Object alpha(int,double,java.lang.Thread)]", declaredMethods(DummySortWithNameDesc.class));
	}
}
