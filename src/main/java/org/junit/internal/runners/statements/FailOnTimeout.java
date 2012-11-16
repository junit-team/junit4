package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;

import java.io.InterruptedIOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.TimeUnit;

public class FailOnTimeout extends Statement {
    private final Statement fOriginalStatement;
    private final TimeUnit fTimeUnit;
    private final long fTimeout;

    public FailOnTimeout(Statement originalStatement, long millis) {
        this(originalStatement, millis, TimeUnit.MILLISECONDS);
    }

    public FailOnTimeout(Statement originalStatement, long timeout, TimeUnit unit) {
        fOriginalStatement = originalStatement;
        fTimeout = timeout;
        fTimeUnit = unit;
    }

    @Override
    public void evaluate() throws Throwable {
        StatementThread thread = evaluateStatement();
        if (!thread.fFinished) {
            throwExceptionForUnfinishedThread(thread);
        }
    }

    private StatementThread evaluateStatement() throws InterruptedException {
        StatementThread thread = new StatementThread(fOriginalStatement);
        thread.setDaemon(true);//Let the process/application complete even if this thread is not finished.
        thread.start();
        fTimeUnit.timedJoin(thread, fTimeout);
        if (!thread.fFinished) {
            thread.recordStackTrace();
        }
        thread.interrupt();
        return thread;
    }

    private void throwExceptionForUnfinishedThread(StatementThread thread)
            throws Throwable {
        if (thread.fExceptionThrownByOriginalStatement != null) {
            throw thread.fExceptionThrownByOriginalStatement;
        } else {
            throwTimeoutException(thread);
        }
    }

    private void throwTimeoutException(StatementThread thread) throws Exception {
        Exception exception = new Exception(String.format(
                "test timed out after %d %s", fTimeout, fTimeUnit.name().toLowerCase()));
        exception.setStackTrace(thread.getRecordedStackTrace());
        throw exception;
    }

    private static class StatementThread extends Thread {
        private final Statement fStatement;

        /**
         * Without modifier of volatile, the values in these variables may
         * become old (default in this case) in {@link FailOnTimeout#evaluate()}.
         * When declaring 'volatile' variables, the CPU is forced
         * to reconcile the values stored in registers and thread's stack
         * by cache coherence at every write-read operation on these variables;
         * Otherwise the CPU and VM may reconcile the memories as it wants
         * (for performance reasons) and therefore these values read in
         * {@link FailOnTimeout#evaluate()} may not be up-to-date without volatile.
         * Besides visibility, the volatile variables have also other guarantees:
         * atomicity and ordering.
         * */
        private volatile boolean fFinished;
        private volatile Throwable fExceptionThrownByOriginalStatement;
        private volatile StackTraceElement[] fRecordedStackTrace;

        public StatementThread(Statement statement) {
            fFinished = false;
            fExceptionThrownByOriginalStatement = null;
            fRecordedStackTrace = null;
            fStatement = statement;
        }

        public void recordStackTrace() {
            fRecordedStackTrace = getStackTrace();
        }

        public StackTraceElement[] getRecordedStackTrace() {
            return fRecordedStackTrace;
        }

        @Override
        public void run() {
            try {
                fStatement.evaluate();
                fFinished = true;
            } catch (InterruptedException e) {
                // don't log the InterruptedException
            } catch (InterruptedIOException e) {
                // don't log the InterruptedIOException
            } catch (ClosedByInterruptException e) {
                // don't log the ClosedByInterruptException
            } catch (Throwable e) {
                fExceptionThrownByOriginalStatement = e;
            }
        }
    }
}