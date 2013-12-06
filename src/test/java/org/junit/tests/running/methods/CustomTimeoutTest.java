package org.junit.tests.running.methods;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.junit.tests.running.methods.TimeoutTest.InfiniteLoopMultithreaded;

public class CustomTimeoutTest {

    public static class CustomTimeoutHandler {

        private static Logger log = Logger.getLogger("CustomTimeoutHandler");

        public Exception handleTimeout(Thread thread) {
            String prefix = "[" + getClass().getSimpleName() + "] ";
            log.warning(prefix + "Test ran into timeout, here is a full thread dump:\n" + getFullThreadDump());
            return new Exception(prefix + "Appears to be stuck => Full thread dump is logged as warning");
        }

        private String getFullThreadDump() {
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

        private String threadToHeaderString(Thread thread) {
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


    public static class CustomFailOnTimeout extends FailOnTimeout {

        private CustomTimeoutHandler handler = new CustomTimeoutHandler();

        public CustomFailOnTimeout(Statement base, long timeout,
                TimeUnit unit, boolean lookForStuckThread) {
            super(base, timeout, unit, lookForStuckThread);
        }

        @Override
        protected Exception createTimeoutException(Thread thread) {
            ArrayList<Throwable> exceptions = new ArrayList<Throwable>();

            Exception handlerException = handler.handleTimeout(thread);

            Exception defaultException = super.createTimeoutException(thread);
            if (defaultException instanceof MultipleFailureException) {
                MultipleFailureException defaultMfe = (MultipleFailureException) defaultException;
                exceptions.addAll(defaultMfe.getFailures());
            } else {
                exceptions.add(defaultException);
            }

            exceptions.add(handlerException);

            return new MultipleFailureException(exceptions);
        }
    }

    public static class CustomTimeout extends Timeout {

        public CustomTimeout(int timeout, TimeUnit unit) {
            super (timeout, unit);
        }

        @Override
        public Statement apply(Statement base, Description description) {
            return new CustomFailOnTimeout(base, fTimeout, fTimeUnit, fLookForStuckThread);
        }
    }


    public static class InfiniteLoopTest {

        @Rule
        public TestRule globalTimeout = new CustomTimeout(100, TimeUnit.MILLISECONDS);

        @Test
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
        assertEquals(2, result.getFailureCount());
        Throwable exception[] = new Throwable[2];
        for (int i = 0; i < 2; i++)
            exception[i] = result.getFailures().get(i).getException();
        assertThat(exception[0].getMessage(), containsString("test timed out after 100 milliseconds"));
        assertThat(exception[1].getMessage(), containsString("[CustomTimeoutHandler] Appears to be stuck => Full thread dump is logged as warning"));
    }


    // --------------------------------------------------------------------------------------------------
    // Below is the additional code for test scenario when the timeout is also looking for stuck threads:

    public static class InfiniteLoopWithLookForStuckThreadTest {

        @Rule
        public TestRule globalTimeout = new CustomTimeout(100, TimeUnit.MILLISECONDS).lookForStuckThread(true);

        @Test
        public void failure() throws Exception {
            (new InfiniteLoopMultithreaded()).failure(false);
        }

    }

    @Test
    public void infiniteLoopwithLookForStuckThread() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(InfiniteLoopWithLookForStuckThreadTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(3, result.getFailureCount());
        Throwable exception[] = new Throwable[3];
        for (int i = 0; i < 3; i++)
            exception[i] = result.getFailures().get(i).getException();
        assertThat(exception[0].getMessage(), containsString("test timed out after 100 milliseconds"));
        assertThat(stackForException(exception[0]), containsString("Thread.join"));
        assertThat(exception[1].getMessage(), containsString("Appears to be stuck in thread timeout-thr2"));
        assertThat(exception[2].getMessage(), containsString("[CustomTimeoutHandler] Appears to be stuck => Full thread dump is logged as warning"));
    }

    private String stackForException(Throwable exception) {
        Writer buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);
        exception.printStackTrace(writer);
        return buffer.toString();
    }

}
