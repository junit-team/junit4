package org.junit.tests.experimental.rules;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TimeoutRuleTest {
    private static final ReentrantLock run1Lock = new ReentrantLock();

    public static class HasGlobalLongTimeout {
        public static final StringBuffer logger = new StringBuffer();

        @Rule
        public final TestRule globalTimeout = new Timeout(50L);

        @Test
        public void run1() throws InterruptedException {
            logger.append("run1");
            TimeoutRuleTest.run1Lock.lockInterruptibly();
            TimeoutRuleTest.run1Lock.unlock();
        }

        @Test
        public void run2() throws InterruptedException {
            logger.append("run2");
            Thread.currentThread().join();
        }

        @Test
        public synchronized void run3() throws InterruptedException {
            logger.append("run3");
            wait();
        }
    }

    public static class HasGlobalTimeUnitTimeout {
        public static final StringBuffer logger = new StringBuffer();

        @Rule
        public final TestRule globalTimeout = new Timeout(50, TimeUnit.MILLISECONDS);

        @Test
        public void run1() throws InterruptedException {
            logger.append("run1");
            TimeoutRuleTest.run1Lock.lockInterruptibly();
            TimeoutRuleTest.run1Lock.unlock();
        }

        @Test
        public void run2() throws InterruptedException {
            logger.append("run2");
            Thread.currentThread().join();
        }

        @Test
        public synchronized void run3() throws InterruptedException {
            logger.append("run3");
            wait();
        }
    }

    @Before public void before() {
        run1Lock.lock();
    }

    @After public void after() {
        run1Lock.unlock();
    }

    @Test
    public void timeUnitTimeout() throws InterruptedException {
        HasGlobalTimeUnitTimeout.logger.setLength(0);
        Result result = JUnitCore.runClasses(HasGlobalTimeUnitTimeout.class);
        assertEquals(3, result.getFailureCount());
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run1"));
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run2"));
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run3"));
    }

    @Test
    public void longTimeout() throws InterruptedException {
        HasGlobalLongTimeout.logger.setLength(0);
        Result result = JUnitCore.runClasses(HasGlobalLongTimeout.class);
        assertEquals(3, result.getFailureCount());
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run1"));
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run2"));
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run3"));
    }
}
