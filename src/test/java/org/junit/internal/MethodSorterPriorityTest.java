package org.junit.internal;

import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;


@FixMethodOrder(MethodSorters.SPECIFIED_PRIORITY)
public class MethodSorterPriorityTest {
    private static ExecutorService executorService;

    @BeforeClass
    public static void startExecutorService() {
        executorService = Executors.newFixedThreadPool(1);
    }

    @AfterClass
    public static void shutDownExecutorService() {
        executorService.shutdown();
        executorService = null;
    }

    private static Result runTest(final Class<?> testClass) {
        Future<Result> future = executorService.submit(new Callable<Result>() {
            public Result call() throws Exception {
                JUnitCore core = new JUnitCore();
                return core.run(testClass);
            }
        });

        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not run test " + testClass, e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Could not run test " + testClass, e);
        }
    }

    private static List<String> realMethodList = new LinkedList<String>();

    private static void markCurrentMethodInfo() {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        final int offset = 2;
        realMethodList.add(stackTrace[offset].getMethodName());
    }

    @FixMethodOrder(MethodSorters.SPECIFIED_PRIORITY)
    public static class MethodPriorityClass {

        @Test(priority = 3)
        public void testMethod1() {
            markCurrentMethodInfo();
        }

        @Test(priority = 1)
        public void testMethod2() {
            markCurrentMethodInfo();
        }

        @Test(priority = 2)
        public void testMethod3() {
            markCurrentMethodInfo();
        }
    }

    @Test
    public void testMethodPriority() {
        runTest(MethodPriorityClass.class);
        List<String> expectedMethodList = Arrays.asList("testMethod2", "testMethod3", "testMethod1");
        Assert.assertEquals(expectedMethodList.size(), realMethodList.size());
        for (int i = 0; i < realMethodList.size(); i++) {
            Assert.assertEquals(expectedMethodList.get(i), realMethodList.get(i));
        }
        realMethodList.clear();
    }

}

