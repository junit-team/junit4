package org.junit.tests.experimental.parallel;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Executors;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ParallelMethodTest {
    private static final long TIMEOUT = 15;
    private static volatile Thread fOne = null;
    private static volatile Thread fTwo = null;
    private static volatile CyclicBarrier fSynchronizer;

    public static class Example {
        @Test public void one() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fOne = Thread.currentThread();
        }

        @Test public void two() throws InterruptedException, BrokenBarrierException, TimeoutException {
            if (fSynchronizer != null) fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS);
            fTwo = Thread.currentThread();
        }
    }

    @Before public void init() {
        fOne = null;
        fTwo = null;
    }

    @Test public void infinitivePool() {
        fSynchronizer = new CyclicBarrier(2);
        Result result = JUnitCore.runClasses(ParallelComputer.methods(), Example.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fOne);
        assertNotNull(fTwo);
        assertThat(fOne, is(not(fTwo)));
    }

    @Test public void singleThread() throws InterruptedException {
        fSynchronizer= null;
        Result result= JUnitCore.runClasses(ParallelComputer.methods(Executors.newSingleThreadExecutor()),
                                            Example.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fOne);
        assertNotNull(fTwo);
        HashSet<Thread> threads= new HashSet<Thread>();
        threads.add(fOne);
        threads.add(fTwo);
        assertThat(threads.size(), is(1));
		assertThat(threads.iterator().next(), is(not(Thread.currentThread())));
    }

    @Test public void fixedSizePool() throws InterruptedException {
        fSynchronizer= new CyclicBarrier(2);
        Result result= JUnitCore.runClasses(ParallelComputer.methods(Executors.newFixedThreadPool(2)),
                                            Example.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fOne);
        assertNotNull(fTwo);
        assertThat(fOne, is(not(fTwo)));
    }
}
