package org.junit.internal;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class MethodSorterTest {

    @Test public void getDeclaredMethods() throws Exception {
        assertEquals("[void epsilon(), void beta(int[][]), java.lang.Object alpha(int,double,java.lang.Thread), void delta(), int gamma(), void gamma(boolean)]", declaredMethods(Dummy.class));
        assertEquals("[void testOne()]", declaredMethods(Super.class));
        assertEquals("[void testTwo()]", declaredMethods(Sub.class));
    }

    private static String declaredMethods(Class<?> c) {
        return Arrays.toString(MethodSorter.getDeclaredMethods(c)).replace(c.getName() + '.', "");
    }

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

}
