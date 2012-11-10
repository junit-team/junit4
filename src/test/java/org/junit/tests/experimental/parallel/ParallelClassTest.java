package org.junit.tests.experimental.parallel;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ParallelClassTest {
    private static final long TIMEOUT = 15;
    private static volatile Thread fExample1One = null;
    private static volatile Thread fExample1Two = null;
    private static volatile Thread fExample2One = null;
    private static volatile Thread fExample2Two = null;
    private static volatile CountDownLatch fSynchronizer;

    public static class Example1 {
        @Test
        public void one() throws InterruptedException {
            fSynchronizer.countDown();
            assertTrue(fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            fExample1One = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            fSynchronizer.countDown();
            assertTrue(fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            fExample1Two = Thread.currentThread();
        }
    }

    public static class Example2 {
        @Test
        public void one() throws InterruptedException {
            fSynchronizer.countDown();
            assertTrue(fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            fExample2One = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            fSynchronizer.countDown();
            assertTrue(fSynchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            fExample2Two = Thread.currentThread();
        }
    }

    @Before
    public void init() {
        fExample1One = null;
        fExample1Two = null;
        fExample2One = null;
        fExample2Two = null;
        fSynchronizer = new CountDownLatch(2);
    }

    @Test
    public void testsRunInParallel() {
        Result result = JUnitCore.runClasses(ParallelComputer.classes(), Example1.class, Example2.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(fExample1One);
        assertNotNull(fExample1Two);
        assertNotNull(fExample2One);
        assertNotNull(fExample2Two);
        assertThat(fExample1One, is(fExample1Two));
        assertThat(fExample2One, is(fExample2Two));
        assertThat(fExample1One, is(not(fExample2One)));
    }
}
