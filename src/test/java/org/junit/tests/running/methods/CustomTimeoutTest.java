package org.junit.tests.running.methods;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.Statement;
import org.junit.tests.running.methods.TimeoutTest.InfiniteLoopMultithreaded;

public class CustomTimeoutTest {

    public static class CustomTimeoutHandler {

        public Exception handleTimeout(Thread thread) {
        	String prefix = "[" + getClass().getSimpleName() + "] ";
            return new Exception(prefix + "Appears to be stuck due to running into timeout. Here could be some more custom failure context...");
        }

    }


    public static class CustomFailOnTimeout extends FailOnTimeout {

        private CustomTimeoutHandler handler = new CustomTimeoutHandler();

        public CustomFailOnTimeout(Statement base, long timeout,
                TimeUnit unit, boolean lookForStuckThread) {
            super(base, timeout, unit, lookForStuckThread);
        }

        @Override
        protected List<Throwable> createAdditionalTimeoutExceptions(
                Thread thread) {
            List<Throwable> exceptions = super.createAdditionalTimeoutExceptions(thread);
            Throwable handleTimeout = handler.handleTimeout(thread);
            exceptions.add(handleTimeout);
            return exceptions;
        }

    }

    public static class CustomTimeout extends Timeout {

        public CustomTimeout(int timeout, TimeUnit unit) {
            this(timeout, unit, false);
        }

        public CustomTimeout(int timeout, TimeUnit unit, boolean lookForStuckThread) {
            super (new Timeout(timeout, unit), lookForStuckThread);
        }

        @Override
        public Statement apply(Statement base, Description description) {
            return new CustomFailOnTimeout(base, getTimeout(), getTimeUnit(), isLookForStuckThread());
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
        assertThat(exception[1].getMessage(), containsString("[CustomTimeoutHandler] Appears to be stuck due to running into timeout. Here could be some more custom failure context..."));
    }


    // --------------------------------------------------------------------------------------------------
    // Below is the additional code for test scenario when the timeout is also looking for stuck threads:

    public static class InfiniteLoopWithLookForStuckThreadTest {

        @Rule
        public TestRule globalTimeout = new CustomTimeout(100, TimeUnit.MILLISECONDS, true);

        @Test
        public void failure() throws Exception {
            (new InfiniteLoopMultithreaded()).failure(false);
        }

    }

    @Test
    public void infiniteLoopWithLookForStuckThread() throws Exception {
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
        assertThat(exception[2].getMessage(), containsString("[CustomTimeoutHandler] Appears to be stuck due to running into timeout. Here could be some more custom failure context..."));
    }

    private String stackForException(Throwable exception) {
        Writer buffer = new StringWriter();
        PrintWriter writer = new PrintWriter(buffer);
        exception.printStackTrace(writer);
        return buffer.toString();
    }

}
