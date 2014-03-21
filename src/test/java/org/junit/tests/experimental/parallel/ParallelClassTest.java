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
    private static volatile Thread example1One = null;
    private static volatile Thread example1Two = null;
    private static volatile Thread example2One = null;
    private static volatile Thread example2Two = null;
    private static volatile CountDownLatch synchronizer;

    public static class Example1 {
        @Test
        public void one() throws InterruptedException {
            synchronizer.countDown();
            assertTrue(synchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            example1One = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            synchronizer.countDown();
            assertTrue(synchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            example1Two = Thread.currentThread();
        }
    }

    public static class Example2 {
        @Test
        public void one() throws InterruptedException {
            synchronizer.countDown();
            assertTrue(synchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            example2One = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            synchronizer.countDown();
            assertTrue(synchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            example2Two = Thread.currentThread();
        }
    }

    @Before
    public void init() {
        example1One = null;
        example1Two = null;
        example2One = null;
        example2Two = null;
        synchronizer = new CountDownLatch(2);
    }

    @Test
    public void testsRunInParallel() {
        Result result = JUnitCore.runClasses(ParallelComputer.classes(), Example1.class, Example2.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(example1One);
        assertNotNull(example1Two);
        assertNotNull(example2One);
        assertNotNull(example2Two);
        assertThat(example1One, is(example1Two));
        assertThat(example2One, is(example2Two));
        assertThat(example1One, is(not(example2One)));
    }
}
