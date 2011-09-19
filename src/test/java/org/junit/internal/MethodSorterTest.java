package org.junit.internal;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class MethodSorterTest {

    @Test public void getDeclaredMethods() throws Exception {
        assertEquals("[java.lang.Object a(int,double,java.lang.Thread), void b(int[][]), int c(), void c(boolean)]", declaredMethods(Dummy.class));
        class Super {
            void testOne() {}
        }
        class Sub extends Super {
            void testTwo() {}
        }
        assertEquals("[void testOne()]", declaredMethods(Super.class));
        assertEquals("[void testTwo()]", declaredMethods(Sub.class));
    }

    private static String declaredMethods(Class<?> c) {
        return Arrays.toString(MethodSorter.getDeclaredMethods(c)).replace(c.getName() + '.', "");
    }

    @Test public void methodNamesAndDescriptors() throws Exception {
        assertEquals("[a~(IDLjava/lang/Thread;)Ljava/lang/Object;, b~([[I)V, c~()I, c~(Z)V]",
                MethodSorter.methodNamesAndDescriptors(Dummy.class).toString());
    }

    @Test public void nameAndDescriptor() throws Exception {
        assertEquals("a~(IDLjava/lang/Thread;)Ljava/lang/Object;", MethodSorter.nameAndDescriptor(Dummy.class.getDeclaredMethod("a", int.class, double.class, Thread.class)));
        assertEquals("b~([[I)V", MethodSorter.nameAndDescriptor(Dummy.class.getDeclaredMethod("b", int[][].class)));
        assertEquals("c~()I", MethodSorter.nameAndDescriptor(Dummy.class.getDeclaredMethod("c")));
        assertEquals("c~(Z)V", MethodSorter.nameAndDescriptor(Dummy.class.getDeclaredMethod("c", boolean.class)));
    }

    private static class Dummy {
        Object a(int i, double d, Thread t) {return null;}
        void b(int[][] x) {}
        int c() {return 0;}
        void c(boolean b) {}
    }

}
