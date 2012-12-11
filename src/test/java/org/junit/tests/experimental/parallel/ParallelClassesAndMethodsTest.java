package org.junit.tests.experimental.parallel;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.ParallelComputer;
import org.junit.rules.TestName;
import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.HashSet;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Testing ParallelComputer.
 * <p/>
 *
 * @author tibor17
 * @since 4.11, 29.9.2012, 14:58
 */
public final class ParallelClassesAndMethodsTest {
    private static final long TIMEOUT= 15;

    private static volatile Thread fExample1One= null;
    private static volatile Thread fExample1Two= null;
    private static volatile Thread fExample2One= null;
    private static volatile Thread fExample2Two= null;
    private static volatile Thread fExample3One= null;
    private static volatile Thread fExample3Two= null;
    private static volatile CyclicBarrier fSynchronizer;

    public static class Example1 {
        @Test public void one() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fExample1One= Thread.currentThread();
        }
        @Test public void two() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fExample1Two= Thread.currentThread();
        }
    }
	
    public static class Example2 {
        @Test public void one() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fExample2One= Thread.currentThread();
        }
        @Test public void two() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fExample2Two= Thread.currentThread();
        }
    }
	
    public static class Example3 {
        @Test public void one() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fExample3One= Thread.currentThread();
        }
        @Test public void two() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fExample3Two= Thread.currentThread();
        }
    }
    
    public static class Erroneous {
        @Rule static final TestName testName= new TestName();
        // intended error in rule -no public instance member
        @Test public void test() {}
    }

    @RunWith(Parameterized.class) public static class FibonacciTest {
        @Parameters(name = "{index}: fib({0})={1}")
        public static Iterable<Object[]> data() {
            return Arrays.asList(new Object[][] {{ 0, 0 }, { 1, 1 }, { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 5 }, { 6, 8 }});
        }

        private int fibonacci(int length) {
            int n2= 0, n1= 0, f;
            do {
                f= n2 + n1;
                n2= n1;
                n1= f;
                if (n1 == 0) n2= 1;
            } while (length-- != 0);
            return f;
        }

        private final int fInput;
        private final int fExpected;

        public FibonacciTest(int input, int expected) {
            fInput= input;
            fExpected= expected;
        }

        @Test public void test1() {
            assertEquals(fExpected, fibonacci(fInput));
        }
        @Test public void test2() {
            assertEquals(fExpected, fibonacci(fInput));
        }
    }

    @Before
    public void init() {
        fExample1One= null;
        fExample1Two= null;
        fExample2One= null;
        fExample2Two= null;
        fExample3One= null;
        fExample3Two= null;
    }

    @Test public void negativeTest() {
        fSynchronizer= null;
        Class<?>[] classes= {Example1.class, Example2.class, Example3.class, Erroneous.class};

        Computer comp= ParallelComputer.methods();
        Result result= JUnitCore.runClasses(comp, classes);
        assertFalse(result.wasSuccessful());

        comp= ParallelComputer.classes();
        result= JUnitCore.runClasses(comp, classes);
        assertFalse(result.wasSuccessful());

        comp= ParallelComputer.classesAndMethodsUnbounded();
        result= JUnitCore.runClasses(comp, classes);
        assertFalse(result.wasSuccessful());

        comp= ParallelComputer.classesAndMethodsBounded(10);
        result= JUnitCore.runClasses(comp, classes);
        assertFalse(result.wasSuccessful());
    }

    @Test public void runParallelOversize() throws InterruptedException {
        // continue with tests if 6 threads for methods are scheduled until timeout; otherwise fail
        fSynchronizer= new CyclicBarrier(6);
        // 11 => more than (six thread parties in barrier + three concurrent classes)
        ThreadPoolExecutor pool= new ThreadPoolExecutor(0, 11,
                                                        // don't reuse threads
                                                        Long.MAX_VALUE, TimeUnit.NANOSECONDS,
                                                        // default like for unbounded pools
                                                        new SynchronousQueue<Runnable>());
        // capacity: 7 concurrent methods and 4 concurrent classes
        Computer comp= ParallelComputer.classesAndMethods(pool, 7);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), is(6));
        // should be 6 == number of thread parties in CyclicBarrier == number of concurrent methods
        // 7 == capacity, we give the Computer a chance to excess the number of concurrent methods if fails
    }

    @Test public void runParallel() throws InterruptedException {
        // continue with tests if 6 threads for methods are scheduled until timeout; otherwise fail
        fSynchronizer= new CyclicBarrier(6);
        // 9 == (six thread parties in barrier + three concurrent classes)
        ThreadPoolExecutor pool= new ThreadPoolExecutor(0, 9,
                                                        // don't reuse threads
                                                        Long.MAX_VALUE, TimeUnit.NANOSECONDS,
                                                        // default like for unbounded pools
                                                        new SynchronousQueue<Runnable>());
        // capacity: 6 concurrent methods and 3 concurrent classes
        Computer comp= ParallelComputer.classesAndMethods(pool, 6);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), is(6));
        // should be 6 == number of thread parties in CyclicBarrier == number of concurrent methods
    }

    @Test public void runParallelUndersized() throws InterruptedException {
        fSynchronizer= null;
        ThreadPoolExecutor pool= new ThreadPoolExecutor(3, 4,
                                                        // reuse threads
                                                        0, TimeUnit.NANOSECONDS,
                                                        // default like for unbounded pools
                                                        new LinkedBlockingQueue<Runnable>());
        // capacity: 2 concurrent methods at least, and 1 or 2 concurrent classes
        Computer comp= ParallelComputer.classesAndMethods(pool, 2);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), anyOf(is(2), is(3)));
    }

    @Test public void runParallelMinimum() throws InterruptedException {
        fSynchronizer= null;
        // capacity: 1 concurrent method, and 1 concurrent class
        Computer comp= ParallelComputer.classesAndMethodsBounded(2);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), anyOf(is(1), is(2)));
		// Might be two method Threads in entire run time, but cannot be concurrent.
        // e.g. class Thread might be used for Method, and vice versa at some later time.
        // Thus the Threads may interchange, but still one Thread per class, and one Thread per method.
    }

    @Test public void classesAndMethodsUnbounded() {
        // continue with tests if 6 threads for methods are scheduled until timeout; otherwise fail
        fSynchronizer= new CyclicBarrier(6);
        Computer comp= ParallelComputer.classesAndMethodsUnbounded();
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), is(6));
    }

    @Test public void classesAndMethodsBounded() throws InterruptedException {
        // continue with tests if 6 threads for methods are scheduled until timeout; otherwise fail
        fSynchronizer= new CyclicBarrier(6);
        // min capacity: 6 concurrent methods, and 3 concurrent classes
        Computer comp= ParallelComputer.classesAndMethodsBounded(10);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), is(6));
    }

    @Test public void classesAndMethodsBoundedUndersized() throws InterruptedException {
        fSynchronizer= null;
        // 1 - 3 concurrent methods
        Computer comp= ParallelComputer.classesAndMethodsBounded(4);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), anyOf(is(1), is(2), is(3)));
    }

    @Test public void classesAndMethodsSimpleDoublePools() {
        fSynchronizer= null;
        ExecutorService poolClasses= Executors.newSingleThreadExecutor();
        ExecutorService poolMethods= Executors.newSingleThreadExecutor();
        // nothing much parallelized
        Computer comp= ParallelComputer.classesAndMethods(poolClasses, poolMethods);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), is(1));
    }

    @Test public void classesAndMethodsDoublePools() {
        fSynchronizer= new CyclicBarrier(2);
        ExecutorService poolClasses= Executors.newFixedThreadPool(3);
        ExecutorService poolMethods= Executors.newFixedThreadPool(2);
        // 2 Threads / class
        Computer comp= ParallelComputer.classesAndMethods(poolClasses, poolMethods);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), is(2));
    }

    @Test public void classesAndMethodsBigDoublePools() {
        fSynchronizer= new CyclicBarrier(3);
        ExecutorService poolClasses= Executors.newFixedThreadPool(5);
        ExecutorService poolMethods= Executors.newFixedThreadPool(4);
        // 3 or 4 method Threads
        Computer comp= ParallelComputer.classesAndMethods(poolClasses, poolMethods);
        Result result= JUnitCore.runClasses(comp, Example1.class, Example2.class, Example3.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertNotNull(fExample3One);
        assertNotNull(fExample3Two);
        HashSet<Thread> threads= new HashSet<Thread>();
        Collections.addAll(threads, fExample1One, fExample1Two,
                                    fExample2One, fExample2Two,
                                    fExample3One, fExample3Two);
        assertThat(threads.size(), anyOf(is(3), is(4)));
    }

    @Test public void fibonacci() {
        Computer comp= ParallelComputer.classesAndMethodsUnbounded();
        Result result= JUnitCore.runClasses(comp, FibonacciTest.class);
        assertTrue(result.wasSuccessful());
    }
}
