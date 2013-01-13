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
        // Let the process/application complete after timeout expired.
        thread.setDaemon(true);
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
        /**
         * This is final variable because the statement is set once.
         * Final makes sure that the statement is immediately visible in
         * #run() (other than current thread) after constructor finished.
         */
        private final Statement fStatement;

        /**
         * These two variables are volatile to make sure that the Thread calling #evaluate()
         * can immediately read their values set by this thread.
         * */
        private volatile boolean fFinished;
        private volatile Throwable fExceptionThrownByOriginalStatement;

        // No need for volatile, because written and read by one thread.
        private StackTraceElement[] fRecordedStackTrace;

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