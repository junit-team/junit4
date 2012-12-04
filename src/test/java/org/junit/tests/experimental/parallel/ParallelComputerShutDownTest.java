package org.junit.tests.experimental.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Testing ParallelComputer when a pool or parallel computer is shutdown.
 *
 * @author tibor17
 * @since 4.12
 */
public class ParallelComputerShutDownTest {
    private static volatile boolean fIsShutdown;
    private static volatile boolean fShutdown1;
    private static volatile boolean fShutdown2;
    private static volatile Runnable fShutdownTask;

    @Rule
    public final TestWrapper testControl= new TestWrapper(500, TimeUnit.MILLISECONDS) {
        @Override
        boolean isShutdown() {
            return fIsShutdown;
        }

        @Override
        protected void before() {
            fIsShutdown= false;
            fShutdown1= false;
            fShutdown2= false;
            super.before();
        }

        @Override
        protected void after() {
            super.after();
            fShutdownTask= null;
        }
    };

    @Rule
    public ExpectedException thrown= ExpectedException.none();


    // tests for the test tool @Rule TestWrapper

    @Test
    public void shutdownHangs() throws InterruptedException {
        thrown.handleAssertionErrors().expect(AssertionError.class);
        testControl.setComputer(ParallelComputer.classesAndMethodsUnbounded());
        Thread.sleep(600L);
        fIsShutdown= true;
    }

    @Test
    public void shutdownBroken() {
        thrown.handleAssertionErrors().expect(AssertionError.class);
        thrown.expectMessage("why the test returned without any shutdown?");
        testControl.setComputer(ParallelComputer.classesAndMethodsUnbounded());
    }

    @Test
    public void shutdownOk() {
        testControl.setComputer(ParallelComputer.classesAndMethodsUnbounded());
        fIsShutdown= true;
    }

    // END: tests for the test tool @Rule TestWrapper

    public static class Example1 extends Example2 {
    }

    public static class Example2 {
        @Test
        public void one() {
        }

        @Test
        public void two() {
        }
    }

    public static class Shutdown1 {
        @Test
        public void one() {
            fShutdown1= true;
            fShutdownTask.run();
        }
    }

    public static class Shutdown2 {
        @Test
        public void two() {
            fShutdown2= true;
        }
    }

    @Test
    public void shutdownBothBeforeStart() {
        thrown.expect(IllegalStateException.class);
        ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        pool.shutdown();
        JUnitCore.runClasses(ParallelComputer.classesAndMethods(pool, 1), Example1.class, Example2.class);
        fail();
    }

    @Test
    public void shutdownClassesBeforeStart() {
        thrown.expect(IllegalStateException.class);
        ExecutorService pool= Executors.newFixedThreadPool(1);
        pool.shutdown();
        JUnitCore.runClasses(ParallelComputer.classes(pool), Example1.class, Example2.class);
        fail();
    }

    @Test
    public void shutdownMethodsBeforeStart() {
        thrown.expect(IllegalStateException.class);
        ExecutorService pool= Executors.newFixedThreadPool(1);
        pool.shutdown();
        JUnitCore.runClasses(ParallelComputer.methods(pool), Example1.class, Example2.class);
        fail();
    }

    @Test
    public void classesAndMethodsShutdownFirstTest() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        ParallelComputer computer= ParallelComputer.classesAndMethods(pool, 1);
        testControl.setComputer(computer);
        fShutdownTask= createShutdownTask(pool);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    @Test
    public void classesAndMethodsShutdownNowFirstTest() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        ParallelComputer computer= ParallelComputer.classesAndMethods(pool, 1);
        testControl.setComputer(computer);
        fShutdownTask= createShutdownNowTask(pool);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    @Test
    public void classesAndMethodsShutdownSecondTest() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        ParallelComputer computer= ParallelComputer.classesAndMethods(pool, 1);
        testControl.setComputer(computer);
        fShutdownTask= createShutdownTask(pool);
        Class<?>[] tests= {Shutdown2.class, Shutdown1.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertTrue(fShutdown2);
    }

    @Test
    public void classesAndMethodsShutdownNowSecondTest() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        ParallelComputer computer= ParallelComputer.classesAndMethods(pool, 1);
        testControl.setComputer(computer);
        fShutdownTask= createShutdownTask(pool);
        Class<?>[] tests= {Shutdown2.class, Shutdown1.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertTrue(fShutdown2);
    }

    @Test
    public void classesAndMethodsSinglePoolShutdown() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(6, 6,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        ParallelComputer computer= ParallelComputer.classesAndMethods(pool, 3);
        testControl.setComputer(computer);
        fShutdownTask= createShutdownTask(pool);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesAndMethodsSinglePoolShutdownNow() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(6, 6,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        ParallelComputer computer= ParallelComputer.classesAndMethods(pool, 3);
        testControl.setComputer(computer);
        fShutdownTask= createShutdownTask(pool);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesAndMethodsShutdown() {
        ExecutorService classPool= Executors.newFixedThreadPool(3);
        ExecutorService methodPool= Executors.newFixedThreadPool(3);
        ParallelComputer computer= ParallelComputer.classesAndMethods(classPool, methodPool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesAndMethodsShutdownNow() {
        ExecutorService classPool= Executors.newFixedThreadPool(3);
        ExecutorService methodPool= Executors.newFixedThreadPool(3);
        ParallelComputer computer= ParallelComputer.classesAndMethods(classPool, methodPool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesInfinitePoolShutdown() {
        ExecutorService pool= Executors.newCachedThreadPool();
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesInfinitePoolShutdownNow() {
        ExecutorService pool= Executors.newCachedThreadPool();
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesSimplePoolShutdown() {
        ExecutorService pool= Executors.newSingleThreadExecutor();
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesSimplePoolShutdownNow() {
        ExecutorService pool= Executors.newSingleThreadExecutor();
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesShutdown() {
        ExecutorService pool= Executors.newFixedThreadPool(2);
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesShutdownNow() {
        ExecutorService pool= Executors.newFixedThreadPool(2);
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesBigPoolShutdown() {
        ExecutorService pool= Executors.newFixedThreadPool(3);
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void classesBigPoolShutdownNow() {
        ExecutorService pool= Executors.newFixedThreadPool(3);
        ParallelComputer computer= ParallelComputer.classes(pool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
    }

    @Test
    public void methodsInfinitePoolShutdown1() {
        final ExecutorService pool= Executors.newCachedThreadPool();
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    @Test
    public void methodsInfinitePoolShutdown1Now() {
        final ExecutorService pool= Executors.newCachedThreadPool();
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    @Test
    public void methodsInfinitePoolShutdown2() {
        final ExecutorService pool= Executors.newCachedThreadPool();
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown2.class, Shutdown1.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertTrue(fShutdown2);
    }

    @Test
    public void methodsInfinitePoolShutdown2Now() {
        final ExecutorService pool= Executors.newCachedThreadPool();
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown2.class, Shutdown1.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertTrue(fShutdown2);
    }

    @Test
    public void methodsSimplePool() {
        final ExecutorService pool= Executors.newSingleThreadExecutor();
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    @Test
    public void methods() {
        ExecutorService pool= Executors.newFixedThreadPool(2);
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    @Test
    public void methodsBigPoolShutdown() {
        ExecutorService pool= Executors.newFixedThreadPool(3);
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    @Test
    public void methodsBigPoolShutdownNow() {
        ExecutorService pool= Executors.newFixedThreadPool(3);
        ParallelComputer computer= ParallelComputer.methods(pool);
        fShutdownTask= createShutdownNowTask(computer);
        Class<?>[] tests= {Shutdown1.class, Shutdown2.class};
        Result result= JUnitCore.runClasses(computer, tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutdown1);
        assertFalse(fShutdown2);
    }

    private static Runnable createShutdownTask(final ExecutorService pool) {
        return new Runnable() {
            public void run() {
                pool.shutdown();
                fIsShutdown= true;
            }
        };
    }

    private static Runnable createShutdownNowTask(final ExecutorService pool) {
        return new Runnable() {
            public void run() {
                pool.shutdownNow();
                fIsShutdown= true;
            }
        };
    }

    private static Runnable createShutdownTask(final ParallelComputer computer) {
        return new Runnable() {
            public void run() {
                computer.shutdown(false);
                fIsShutdown= true;
            }
        };
    }

    private static Runnable createShutdownNowTask(final ParallelComputer computer) {
        return new Runnable() {
            public void run() {
                computer.shutdown(true);
                fIsShutdown= true;
            }
        };
    }
}
