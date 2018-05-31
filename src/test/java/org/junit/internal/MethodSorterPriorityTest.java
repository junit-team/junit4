package org.junit.internal;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Method;


@FixMethodOrder(MethodSorters.SPECIFIED_PRIORITY)
public class MethodSorterPriorityTest {

    @Test(priority = 10)
    public void testM2() throws Exception {
        printMethodInfo();
    }

    @Test(priority = 2)
    public void testM3() throws Exception {
        printMethodInfo();
    }

    @Test
    public void testM1() throws Exception {
        printMethodInfo();
    }

    @Test(priority = -10)
    public void testA10() throws Exception {
        printMethodInfo();
    }

    private void printMethodInfo() throws NoSuchMethodException {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        final int offset = 1;
        StringBuilder info = new StringBuilder();
        info.append(stackTrace[offset].getClassName())
                .append(".").append(stackTrace[offset].getMethodName());
        Method m = this.getClass().getMethod(stackTrace[offset].getMethodName());
        Test ann = m.getAnnotation(Test.class);
        info.append(" -> ").append(ann.priority());
        System.out.println(info);
    }
}

