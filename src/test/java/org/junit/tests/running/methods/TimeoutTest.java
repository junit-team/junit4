package org.junit.tests.running.methods;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class TimeoutTest {

    public static class FailureWithTimeoutTest {
        @Test(timeout = 1000)
        public void failure() {
            fail();
        }
    }

    @Test
    public void failureWithTimeout() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(FailureWithTimeoutTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(AssertionError.class, result.getFailures().get(0).getException().getClass());
    }

    public static class FailureWithTimeoutRunTimeExceptionTest {
        @Test(timeout = 1000)
        public void failure() {
            throw new NullPointerException();
        }
    }

    @Test
    public void failureWithTimeoutRunTimeException() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(FailureWithTimeoutRunTimeExceptionTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(NullPointerException.class, result.getFailures().get(0).getException().getClass());
    }

    public static class SuccessWithTimeoutTest {
        @Test(timeout = 1000)
        public void success() {
        }
    }

    @Test
    public void successWithTimeout() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(SuccessWithTimeoutTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
    }

    public static class TimeoutFailureTest {
        @Test(timeout = 100)
        public void success() throws InterruptedException {
            Thread.sleep(40000);
        }
    }

    @Ignore("was breaking gump")
    @Test
    public void timeoutFailure() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeoutFailureTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(InterruptedException.class, result.getFailures().get(0).getException().getClass());
    }

    public static class InfiniteLoopTest {
        @Test(timeout = 100)
        public void failure() {
            infiniteLoop();
        }

        private void infiniteLoop() {
            for (; ; ) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Test
    public void infiniteLoop() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(InfiniteLoopTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        Throwable exception = result.getFailures().get(0).getException();
        assertTrue(exception.getMessage().contains("test timed out after 100 milliseconds"));
    }

    public static class ImpatientLoopTest {
        @Test(timeout = 1)
        public void failure() {
            infiniteLoop();
        }

        private void infiniteLoop() {
            for (; ; ) ;
        }
    }

    @Ignore("This breaks sporadically with time differences just slightly more than 200ms")
    @Test
    public void infiniteLoopRunsForApproximatelyLengthOfTimeout() throws Exception {
        // "prime the pump": running these beforehand makes the runtimes more predictable
        //                   (because of class loading?)
        JUnitCore.runClasses(InfiniteLoopTest.class, ImpatientLoopTest.class);
        long longTime = runAndTime(InfiniteLoopTest.class);
        long shortTime = runAndTime(ImpatientLoopTest.class);
        long difference = longTime - shortTime;
        assertTrue(String.format("Difference was %sms", difference), difference < 200);
    }

    private long runAndTime(Class<?> clazz) {
        JUnitCore core = new JUnitCore();
        long startTime = System.currentTimeMillis();
        core.run(clazz);
        long totalTime = System.currentTimeMillis() - startTime;
        return totalTime;
    }

    private String stackForException(Throwable exception) {
        Writer buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);
        exception.printStackTrace(writer);
        return buffer.toString();
    }
    
    @Test
    public void stalledThreadAppearsInStackTrace() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(InfiniteLoopTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        Throwable exception = result.getFailures().get(0).getException();
        assertThat(stackForException(exception), containsString("infiniteLoop")); // Make sure we have the stalled frame on the stack somewhere
    }

    public static class InfiniteLoopMultithreaded {
        
        private static class ThreadTest implements Runnable {
            private boolean fStall;

            public ThreadTest(boolean stall) {
                fStall = stall;
            }

            public void run() {
                if (fStall)
                    for (; ; ) ;   
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
            }
        }
        
        public void failure(boolean mainThreadStalls) throws Exception {
            Thread t1 = new Thread(new ThreadTest(false), "timeout-thr1");
            Thread t2 = new Thread(new ThreadTest(!mainThreadStalls), "timeout-thr2");
            Thread t3 = new Thread(new ThreadTest(false), "timeout-thr3");
            t1.start();
            t2.start();
            t3.start();
            if (mainThreadStalls)
                for (; ; ) ;
            t1.join();
            t2.join();
            t3.join();
        }
   }
    
    public static class InfiniteLoopWithStuckThreadTest {
        @Rule
        public TestRule globalTimeout = Timeout.builder()
            .withTimeout(100, TimeUnit.MILLISECONDS)
            .withLookingForStuckThread(true)
            .build();

        @Test
        public void failure() throws Exception {
            (new InfiniteLoopMultithreaded()).failure(false);
        }
    }
    
    public static class InfiniteLoopStuckInMainThreadTest {
        @Rule
        public TestRule globalTimeout = Timeout.builder()
            .withTimeout(100, TimeUnit.MILLISECONDS)
            .withLookingForStuckThread(true)
            .build();

        @Test
        public void failure() throws Exception {
            (new InfiniteLoopMultithreaded()).failure(true);
        }
    }

    @Test
    public void timeoutFailureMultithreaded() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(InfiniteLoopWithStuckThreadTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        Throwable exception[] = new Throwable[2];
        for (int i = 0; i < 2; i++)
            exception[i] = result.getFailures().get(i).getException();
        assertThat(exception[0].getMessage(), containsString("test timed out after 100 milliseconds"));
        assertThat(stackForException(exception[0]), containsString("Thread.join"));
        assertThat(exception[1].getMessage(), containsString("Appears to be stuck in thread timeout-thr2"));
    }

    @Test
    public void timeoutFailureMultithreadedStuckInMain() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(InfiniteLoopStuckInMainThreadTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        Throwable exception = result.getFailures().get(0).getException();
        assertThat(exception.getMessage(), containsString("test timed out after 100 milliseconds"));
        assertThat(exception.getMessage(), not(containsString("Appears to be stuck")));
    }

    @Test
    public void compatibility() {
        TestResult result = new TestResult();
        new JUnit4TestAdapter(InfiniteLoopTest.class).run(result);
        assertEquals(1, result.errorCount());
    }

    public static class WillTimeOut {
        static boolean afterWasCalled = false;

        @Test(timeout = 1)
        public void test() {
            for (; ; ) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // ok, tests are over
                }
            }
        }

        @After
        public void after() {
            afterWasCalled = true;
        }
    }

    @Test
    public void makeSureAfterIsCalledAfterATimeout() {
        JUnitCore.runClasses(WillTimeOut.class);
        assertThat(WillTimeOut.afterWasCalled, is(true));
    }

    public static class TimeOutZero {
        @Rule
        public Timeout timeout = Timeout.seconds(0);

        @Test
        public void test() {
            try {
                Thread.sleep(200); // long enough to suspend thread execution
            } catch (InterruptedException e) {
                // Don't care
            }
        }
    }

    @Test
    public void testZeroTimeoutIsIgnored() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeOutZero.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should not have failed", 0, result.getFailureCount());
    }

    private static class TimeoutSubclass extends Timeout {

        public TimeoutSubclass(long timeout, TimeUnit timeUnit) {
            super(timeout, timeUnit);
        }

        public long getTimeoutFromSuperclass(TimeUnit unit) {
            return super.getTimeout(unit);
        }
    }

    public static class TimeOutOneSecond {
        @Rule
        public TimeoutSubclass timeout = new TimeoutSubclass(1, TimeUnit.SECONDS);

        @Test
        public void test() {
            assertEquals(1000, timeout.getTimeoutFromSuperclass(TimeUnit.MILLISECONDS));
        }
    }

    @Test
    public void testGetTimeout() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(TimeOutOneSecond.class);
        assertEquals("Should run the test", 1, result.getRunCount());
        assertEquals("Test should not have failed", 0, result.getFailureCount());
    }
}
