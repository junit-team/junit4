package org.junit.tests.running.classes;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import static org.junit.Assert.assertEquals;

public class ThreadsTest {
    private String log = "";

    public static class TestWithInterrupt {

        @Test
        public void interruptCurrentThread() {
            Thread.currentThread().interrupt();
        }

        @Test
        public void otherTestCaseInterruptingCurrentThread() {
            Thread.currentThread().interrupt();
        }

    }

    @Test
    public void currentThreadInterruptedStatusIsClearedAfterEachTestExecution() {
        log = "";
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new RunListener() {
            @Override
            public void testFinished(Description description) {
                log += Thread.currentThread().isInterrupted() + " ";
            }
        });

        Result result = jUnitCore.run(TestWithInterrupt.class);
        assertEquals(0, result.getFailureCount());
        assertEquals("false false ", log);
    }

    @RunWith(BlockJUnit4ClassRunner.class)
    public static class TestWithInterruptFromAfterClass {
        @AfterClass
        public static void interruptCurrentThread() {
            Thread.currentThread().interrupt();
        }

        @Test
        public void test() {
            // no-op
        }
    }

    @Test
    public void currentThreadInterruptStatusIsClearedAfterSuiteExecution() {
        log = "";
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new RunListener() {
            @Override
            public void testSuiteFinished(Description description) throws Exception {
                log += Thread.currentThread().isInterrupted();
            }
        });

        Request request = new Request() {
            @Override
            public Runner getRunner() {
                try {
                    return new BlockJUnit4ClassRunner(TestWithInterruptFromAfterClass.class) {
                    };
                } catch (InitializationError e) {
                    return new ErrorReportingRunner(TestWithInterruptFromAfterClass.class, e);
                }
            }
        };

        Result result = jUnitCore.run(request);
        assertEquals(0, result.getFailureCount());
        assertEquals("false", log);
    }
}
