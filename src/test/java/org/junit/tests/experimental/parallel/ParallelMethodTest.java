package org.junit.tests.experimental.parallel;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ParallelMethodTest {
    private static final long TIMEOUT = 15;
    private static volatile Thread one = null;
    private static volatile Thread two = null;

    public static class Example {
        private static volatile CountDownLatch synchronizer;

        @BeforeClass
        public static void init() {
            synchronizer = new CountDownLatch(2);
        }

        @Test
        public void one() throws InterruptedException {
            synchronizer.countDown();
            assertTrue(synchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            one = Thread.currentThread();
        }

        @Test
        public void two() throws InterruptedException {
            synchronizer.countDown();
            assertTrue(synchronizer.await(TIMEOUT, TimeUnit.SECONDS));
            two = Thread.currentThread();
        }
    }

    @Before
    public void init() {
        one = null;
        two = null;
    }

    @Test
    public void testsRunInParallel() {
        Result result = JUnitCore.runClasses(ParallelComputer.methods(), Example.class);
        assertTrue(result.wasSuccessful());
        assertNotNull(one);
        assertNotNull(two);
        assertThat(one, is(not(two)));
    }
}
