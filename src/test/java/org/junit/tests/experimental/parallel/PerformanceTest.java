package org.junit.tests.experimental.parallel;

import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class PerformanceTest {
    public static class Slow1 {
        @Test
        public void slow1() throws InterruptedException {
            Thread.sleep(900);
        }
        @Test
        public void slow2() throws InterruptedException {
            Thread.sleep(1100);
        }
    }

    public static class SlowAndFast1 {
        @Test
        public void slowest() throws InterruptedException {
            Thread.sleep(2000);
        }
        @Test
        public void slow() throws InterruptedException {
            Thread.sleep(1500);
        }
        @Test
        public void fast() throws InterruptedException {
            Thread.sleep(500);
        }
    }

    public static class Slow2 extends Slow1 {}
    public static class SlowAndFast2 extends SlowAndFast1 {}

    @Test
    public void sequenceSlow() {
        // takes four seconds to complete
        Result result= JUnitCore.runClasses(Slow1.class, Slow2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void sequenceSlowAndFast() {
        // takes four seconds to complete
        Result result= JUnitCore.runClasses(SlowAndFast1.class, SlowAndFast2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClassesAndMethods() {
        // for Javadoc. Takes one second.
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethodsUnbounded(), Slow1.class, Slow2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClasses() {
        // for Javadoc. Takes two seconds.
        Result result= JUnitCore.runClasses(ParallelComputer.classes(), Slow1.class, Slow2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelMethods() {
        // for Javadoc. Takes two seconds.
        Result result= JUnitCore.runClasses(ParallelComputer.methods(), Slow1.class, Slow2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClasses1() {
        // takes two seconds to complete
        Result result= JUnitCore.runClasses(ParallelComputer.classes(), Slow1.class, Slow2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClasses2() {
        // takes two seconds to complete
        Result result= JUnitCore.runClasses(ParallelComputer.classes(Executors.newFixedThreadPool(2)), Slow1.class, Slow2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelMethods1() {
        // takes two seconds to complete, executes three threads
        Result result= JUnitCore.runClasses(ParallelComputer.methods(), SlowAndFast1.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelMethods2() {
        // takes two seconds to complete
        Result result= JUnitCore.runClasses(ParallelComputer.methods(Executors.newFixedThreadPool(2)), SlowAndFast1.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClassesAndMethodsUnbounded() {
        // takes two seconds to complete
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethodsUnbounded(), SlowAndFast1.class, SlowAndFast2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClassesAndMethodsBounded1() {
        // takes two seconds to complete
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethodsBounded(8), SlowAndFast1.class, SlowAndFast2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClassesAndMethodsBounded2() {
        // takes two seconds to complete
        ThreadPoolExecutor pool= new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        Computer cmp= ParallelComputer.classesAndMethods(pool);
        Result result= JUnitCore.runClasses(cmp, SlowAndFast1.class, SlowAndFast2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClassesAndMethods1() {
        // takes 2.5 second to complete
        ThreadPoolExecutor pool= new ThreadPoolExecutor(6, 6, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        Result result= JUnitCore.runClasses(ParallelComputer.classesAndMethods(pool, 4), SlowAndFast1.class, SlowAndFast2.class);
        assertTrue(result.wasSuccessful());
    }

    @Test
    public void parallelClassesAndMethods2() {
        // takes 2.5 second to complete
        Computer cmp= ParallelComputer.classesAndMethods(Executors.newFixedThreadPool(2), Executors.newFixedThreadPool(4));
        Result result= JUnitCore.runClasses(cmp, SlowAndFast1.class, SlowAndFast2.class);
        assertTrue(result.wasSuccessful());
    }
}
