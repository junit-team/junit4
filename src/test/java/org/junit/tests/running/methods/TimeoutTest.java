package org.junit.tests.running.methods;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.rules.TimeoutHandler;
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
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS).lookForStuckThread(true);

        @Test
        public void failure() throws Exception {
            (new InfiniteLoopMultithreaded()).failure(false);
        }
    }

    public static class InfiniteLoopStuckInMainThreadTest {
        @Rule
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS).lookForStuckThread(true);

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


// --- Below are deadlock tests ---
    public static class MyTimeoutHandler implements TimeoutHandler {

        private static Logger log = Logger.getLogger("MyTimeoutHandler");

        public Exception handleTimeout(Thread thread) {
            String prefix = "[" + getClass().getSimpleName() + "] ";
            log.warning(prefix + "Test ran into timeout, here is a full thread dump:\n" + getFullThreadDump());
            return new Exception(prefix + "Appears to be stuck => Full thread dump is logged as warning");
        }

        //TODO: made temporarily static to allow thread dump in teardown showing surviving threads from other tests 
//        private String getFullThreadDump() {
        public static String getFullThreadDump() {
            StringBuilder sb = new StringBuilder();

            // TODO: ThreadMXBean provides interesting thread dump information (locks, monitors, synchronizers) only with Java >= 1.6

            // First try ThreadMXBean#findMonitorDeadlockedThreads():
            ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
            long[] deadlockedThreadIds = threadMxBean.findMonitorDeadlockedThreads();
            if (deadlockedThreadIds != null) {
                sb.append("Found deadlocked threads:");
                ThreadInfo[] threadInfos = threadMxBean.getThreadInfo(deadlockedThreadIds);
                for (ThreadInfo threadInfo : threadInfos) {
                    sb.append("\n\t" + threadInfo.getThreadName() + " Id=" + threadInfo.getThreadId()
                            + " Lock name=" + threadInfo.getLockName() + " Lock owner Id=" + threadInfo.getLockOwnerId()
                            + " Lock owner name=" + threadInfo.getLockOwnerName());
                }
            }

            // Then just the full thread dump:
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            sb.append("Thread dump (total threads=" + allStackTraces.size() + ")");
            for (Thread thread : allStackTraces.keySet()) {
                sb.append("\n\t" + thread.getName());
            }
            sb.append("\n");
            for (Entry<Thread, StackTraceElement[]> threadEntry : allStackTraces.entrySet()) {
                sb.append("\n" + threadToHeaderString(threadEntry.getKey()));

                StackTraceElement[] stackTraces = threadEntry.getValue();
                for (int i = 0; i < stackTraces.length; i++) {
                    StackTraceElement ste = stackTraces[i];
                    sb.append("\tat " + ste.toString());
                    sb.append('\n');
                }
            }

            return sb.toString();
        }

        //TODO: made temporarily static to allow thread dump in teardown showing surviving threads from other tests 
//        private String threadToHeaderString(Thread thread) {
        private static String threadToHeaderString(Thread thread) {
            StringBuilder sb = new StringBuilder("\"" + thread.getName() + "\""
                    + " Id=" + thread.getId() + " Daemon=" + thread.isDaemon()
                    + " State=" + thread.getState() + " Priority=" + thread.getPriority()
                    + " Group=" + thread.getThreadGroup().getName());
            if (thread.isAlive()) {
                sb.append(" (alive)");
            }
            if (thread.isInterrupted()) {
                sb.append(" (interrupted)");
            }
            sb.append('\n');
            return sb.toString();
        }

    }

    public static class MyGlobalTimeoutHandler extends MyTimeoutHandler { }

    public static abstract class AbstractLockedWithDeadlockTest {

        private final ReentrantLock lock1 = new ReentrantLock();
        private final ReentrantLock lock2 = new ReentrantLock();

        protected LockedThread1 thread1;
        protected LockedThread2 thread2;

        @After
        public void teardown() throws InterruptedException {
            thread1.interrupt();
            thread2.interrupt();

            thread1.join();
            thread2.join();

            //TODO: temporary thread dumping in teardown to show surviving threads from other tests 
            System.out.println("In teardown:\n" + MyTimeoutHandler.getFullThreadDump());
        }

        protected class LockedThread1 extends Thread {

            public LockedThread1() {
                super("Thread-locked-1");
            }

            @Override
            public void run() {
                try {
                    lock1.lockInterruptibly();
                    Thread.sleep(50);
                    lock2.lockInterruptibly();
                } catch (InterruptedException e) {
                    System.err.println("Interrupted thread 1: " + e);
                }
            }
        }

        protected class LockedThread2 extends Thread {

            public LockedThread2() {
                super("Thread-locked-2");
            }

            @Override
            public void run() {
                try {
                    lock2.lockInterruptibly();
                    Thread.sleep(50);
                    lock1.lockInterruptibly();
                } catch (InterruptedException e) {
                    System.err.println("Interrupted thread 2: " + e);
                }
            }
        }

        @Test
        public void failure() throws Exception {
            thread1 = new LockedThread1();
            thread2 = new LockedThread2();

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();
        }
    }

    public static class LockedWithDeadlockTest extends AbstractLockedWithDeadlockTest {
        @Rule
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS).customTimeoutHandler(new MyTimeoutHandler());
    }


    @Test
    public void timeoutFailureMultithreadedDeadlockWithFullDump() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(LockedWithDeadlockTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        Throwable exception[] = new Throwable[2];
        for (int i = 0; i < 2; i++)
            exception[i] = result.getFailures().get(i).getException();
        assertThat(exception[0].getMessage(), containsString("test timed out after 100 milliseconds"));
        assertThat(stackForException(exception[0]), containsString("Thread.join"));
        assertThat(exception[1].getMessage(), containsString("[MyTimeoutHandler] Appears to be stuck => Full thread dump is logged as warning"));
    }


    public static class LockedWithDeadlockWithNoDedicatedTimeoutHandlerTest extends AbstractLockedWithDeadlockTest {
        @Rule
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS);
    }


    @Test
    public void timeoutFailureMultithreadedDeadlockWithFullDumpDueToGlobalTimeoutHandler() throws Exception {
        try {
            System.setProperty(Timeout.TIMEOUT_HANDLER_CLASS_NAME_PROPERTY_NAME, "org.junit.tests.running.methods.TimeoutTest$MyGlobalTimeoutHandler");

            JUnitCore core = new JUnitCore();
            Result result = core.run(LockedWithDeadlockWithNoDedicatedTimeoutHandlerTest.class);
            assertEquals(1, result.getRunCount());
            assertEquals(2, result.getFailureCount());
            Throwable exception[] = new Throwable[2];
            for (int i = 0; i < 2; i++)
                exception[i] = result.getFailures().get(i).getException();
            assertThat(exception[0].getMessage(), containsString("test timed out after 100 milliseconds"));
            assertThat(stackForException(exception[0]), containsString("Thread.join"));
            assertThat(exception[1].getMessage(), containsString("[MyGlobalTimeoutHandler] Appears to be stuck => Full thread dump is logged as warning"));
        } finally {
            System.clearProperty(Timeout.TIMEOUT_HANDLER_CLASS_NAME_PROPERTY_NAME);
        }
    }


    public static class LockedWithDeadlockWithPartiallyDedicatedTimeoutHandlerTest extends AbstractLockedWithDeadlockTest {
        @Rule
        public TestRule globalTimeout = new Timeout(100, TimeUnit.MILLISECONDS);

        @Test(timeout = 70)
        public void failure2() throws Exception {
            thread1 = new LockedThread1();
            thread2 = new LockedThread2();

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();
        }

    }


    /**
     * Because test case {@link LockedWithDeadlockWithPartiallyDedicatedTimeoutHandlerTest#failure2()}
     * has a test method timeout, it gets a specific timeout rule.
     * <p>
     * TODO: green in Eclipse, but red in Ant.
     */
    @Test
    public void timeoutFailureMultithreadedDeadlockWithPartiallyFullDumpDueToGlobalTimeoutHandler() throws Exception {
        try {
            System.setProperty(Timeout.TIMEOUT_HANDLER_CLASS_NAME_PROPERTY_NAME, "org.junit.tests.running.methods.TimeoutTest$MyGlobalTimeoutHandler");

            JUnitCore core = new JUnitCore();
            Result result = core.run(LockedWithDeadlockWithPartiallyDedicatedTimeoutHandlerTest.class);
            assertEquals(2, result.getRunCount());
            assertEquals(4, result.getFailureCount());
            Throwable exception[] = new Throwable[4];
            //TODO: in Ant execution this test fails; and there are quite some deadlocked threads surviving/around from other tests:
            // this is just some debug output
            for (int i = 0; i < 4; i++) {
                System.err.println(i + ":" + result.getFailures().get(i));
            }
            // end of debug output

            for (int i = 0; i < 4; i++)
                exception[i] = result.getFailures().get(i).getException();

            //TODO: this fails in Ant context!
            assertThat(exception[0].getMessage(), containsString("test timed out after 70 milliseconds"));
            assertThat(stackForException(exception[0]), containsString("Thread.join"));
            assertThat(exception[1].getMessage(), containsString("[MyGlobalTimeoutHandler] Appears to be stuck => Full thread dump is logged as warning"));

            assertThat(exception[2].getMessage(), containsString("test timed out after 100 milliseconds"));
            assertThat(stackForException(exception[2]), containsString("Thread.join"));
            assertThat(exception[3].getMessage(), containsString("[MyGlobalTimeoutHandler] Appears to be stuck => Full thread dump is logged as warning"));
        } finally {
            System.clearProperty(Timeout.TIMEOUT_HANDLER_CLASS_NAME_PROPERTY_NAME);
        }
    }
// --- Above are deadlock tests ---

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
}
