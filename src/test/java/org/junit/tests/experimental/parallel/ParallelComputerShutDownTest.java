package org.junit.tests.experimental.parallel;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Testing ParallelComputer when a pool is shut down externally.
 *
 * @author tibor17
 * @since 4.12
 */
public class ParallelComputerShutDownTest {
    private static CountDownLatch fTrigger;
    private static volatile boolean fIsShutDown;
    private static volatile boolean fShutDown1;
    private static volatile boolean fShutDown2;

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

    public static class ShutDown1 {
        @Test
        public void one() throws InterruptedException {
            fShutDown1= true;
            fTrigger.countDown();
            while(!fIsShutDown) {
                Thread.yield();
            }
        }
    }

    public static class ShutDown2 {
        @Test
        public void two() {
            fShutDown2= true;
        }
    }


    @Before
    public void init() {
        //binary semaphore triggers one shut-down operation
        fTrigger= new CountDownLatch(1);
        fIsShutDown= false;
        fShutDown1= false;
        fShutDown2= false;
    }

    @Test(expected= IllegalStateException.class)
    public void shutDownBothBeforeStart() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        pool.shutdown();
        JUnitCore.runClasses(ParallelComputer.classesAndMethods(pool, 1), Example1.class, Example2.class);
        fail();
    }

    @Test(expected= IllegalStateException.class)
    public void shutDownClassesBeforeStart() {
        ExecutorService pool= Executors.newFixedThreadPool(1);
        pool.shutdown();
        JUnitCore.runClasses(ParallelComputer.classes(pool), Example1.class, Example2.class);
        fail();
    }

    @Test(expected= IllegalStateException.class)
    public void shutDownMethodsBeforeStart() {
        ExecutorService pool= Executors.newFixedThreadPool(1);
        pool.shutdown();
        JUnitCore.runClasses(ParallelComputer.methods(pool), Example1.class, Example2.class);
        fail();
    }

    @Test
    public void classesAndMethodsShutdownFirstTest() {
        final ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethods(pool, 1), tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutDown1);
        assertFalse(fShutDown2);
    }

    @Test
    public void classesAndMethodsShutdownSecondTest() {
        final ThreadPoolExecutor pool= new ThreadPoolExecutor(2, 2,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown2.class, ShutDown1.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethods(pool, 1), tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutDown1);
        assertTrue(fShutDown2);
    }

    @Test
    public void classesAndMethodsSinglePool() {
        final ThreadPoolExecutor pool= new ThreadPoolExecutor(6, 6,
                                      0L, TimeUnit.NANOSECONDS,
                                      new LinkedBlockingQueue<Runnable>());
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethods(pool, 3), tests);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void classesAndMethodsKillClasses() {
        final ExecutorService classPool= Executors.newFixedThreadPool(3);
        final ExecutorService methodPool= Executors.newFixedThreadPool(3);
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                classPool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethods(classPool, methodPool), tests);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void classesAndMethodsKillMethods() {
        final ExecutorService classPool= Executors.newFixedThreadPool(3);
        final ExecutorService methodPool= Executors.newFixedThreadPool(3);
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                methodPool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethods(classPool, methodPool), tests);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void classesInfinitePool() {
        final ExecutorService pool= Executors.newCachedThreadPool();
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classes(pool), tests);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void classesSimplePool() {
        final ExecutorService pool= Executors.newSingleThreadExecutor();
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classes(pool), tests);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void classes() {
        final ExecutorService pool= Executors.newFixedThreadPool(2);
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classes(pool), tests);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void classesBigPool() {
        final ExecutorService pool= Executors.newFixedThreadPool(3);
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.classes(pool), tests);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void methodsInfinitePool() {
        final ExecutorService pool= Executors.newCachedThreadPool();
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.methods(pool), tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutDown1);
        assertFalse(fShutDown2);
    }

    @Test
    public void methodsSimplePool() {
        final ExecutorService pool= Executors.newSingleThreadExecutor();
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.methods(pool), tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutDown1);
        assertFalse(fShutDown2);
    }

    @Test
    public void methods() {
        final ExecutorService pool= Executors.newFixedThreadPool(2);
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.methods(pool), tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutDown1);
        assertFalse(fShutDown2);
    }

    @Test
    public void methodsBigPool() {
        final ExecutorService pool= Executors.newFixedThreadPool(3);
        Executors.newSingleThreadExecutor().submit(new Callable<Void>() {
            public Void call() throws InterruptedException {
                fTrigger.await();
                pool.shutdown();
                fIsShutDown= true;
                return null;
            }
        });
        Class<?>[] tests= {ShutDown1.class, ShutDown2.class};
        Result result= JUnitCore.runClasses(ParallelComputer.methods(pool), tests);
        assertTrue(result.wasSuccessful());
        assertTrue(fShutDown1);
        assertFalse(fShutDown2);
    }
}
