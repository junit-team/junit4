package org.junit.internal.runners.statements;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.rules.Timeout;
import org.junit.rules.TimeoutHandler;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

public class FailOnTimeout extends Statement {
    private final Statement fOriginalStatement;
    private final TimeUnit fTimeUnit;
    private final long fTimeout;
    private final boolean fLookForStuckThread;
    private final TimeoutHandler fTimeoutHandler;
    private ThreadGroup fThreadGroup = null;

    public FailOnTimeout(Statement originalStatement, long millis) {
        this(originalStatement, millis, TimeUnit.MILLISECONDS);
    }

    public FailOnTimeout(Statement originalStatement, long timeout, TimeUnit unit) {
        this(originalStatement, timeout, unit, false, null);
    }

    public FailOnTimeout(Statement originalStatement, long timeout, TimeUnit unit, boolean lookForStuckThread, TimeoutHandler timeoutHandler) {
        fOriginalStatement = originalStatement;
        fTimeout = timeout;
        fTimeUnit = unit;
        fLookForStuckThread = lookForStuckThread;
        fTimeoutHandler = timeoutHandler;
    }

    @Override
    public void evaluate() throws Throwable {
        FutureTask<Throwable> task = new FutureTask<Throwable>(new CallableStatement());
        fThreadGroup = new ThreadGroup("FailOnTimeoutGroup");
        Thread thread = new Thread(fThreadGroup, task, "Time-limited test");
        thread.setDaemon(true);
        thread.start();
        Throwable throwable = getResult(task, thread);
        if (throwable != null) {
            throw throwable;
        }
    }

    /**
     * Wait for the test task, returning the exception thrown by the test if the
     * test failed, an exception indicating a timeout if the test timed out, or
     * {@code null} if the test passed.
     */
    private Throwable getResult(FutureTask<Throwable> task, Thread thread) {
        try {
            return task.get(fTimeout, fTimeUnit);
        } catch (InterruptedException e) {
            return e; // caller will re-throw; no need to call Thread.interrupt()
        } catch (ExecutionException e) {
            // test failed; have caller re-throw the exception thrown by the test
            return e.getCause();
        } catch (TimeoutException e) {
            return createTimeoutException(thread);
        }
    }

    private Exception createTimeoutException(Thread thread) {
        StackTraceElement[] stackTrace = thread.getStackTrace();
        final Thread stuckThread = fLookForStuckThread ? getStuckThread(thread) : null;
        Exception currThreadException = new Exception(String.format(
                "test timed out after %d %s", fTimeout, fTimeUnit.name().toLowerCase()));
        if (stackTrace != null) {
            currThreadException.setStackTrace(stackTrace);
            thread.interrupt();
        }

        List<Throwable> exceptions = new ArrayList<Throwable>();
        exceptions.add(currThreadException);

        if (stuckThread != null) {
            Exception stuckThreadException =
                new Exception ("Appears to be stuck in thread " +
                               stuckThread.getName());
            stuckThreadException.setStackTrace(getStackTrace(stuckThread));
            exceptions.add(stuckThreadException);
        }

        // Specifically set timeout handler takes precedence
        TimeoutHandler timeoutHandler = fTimeoutHandler;
        if (timeoutHandler == null) {
            timeoutHandler = lookForGlobalTimeoutHandler();
        }
        if (timeoutHandler != null) {
            // For the sake of convenience just allow adding another exception by the custom timeout handler
            Exception timeoutHandlerException = timeoutHandler.handleTimeout(thread);
            if (timeoutHandlerException != null) {
                exceptions.add(timeoutHandlerException);
            }
        }

        if (exceptions.size() > 1) {
            return new MultipleFailureException(exceptions);
        } else {
            return currThreadException;
        }
    }

    /**
     * Note: Before this was in {@link Timeout} and called from
     * {@link Timeout#apply(Statement, org.junit.runner.Description)}, but this would not have
     * been used for timeouts specified for single test cases.
     */
    private TimeoutHandler lookForGlobalTimeoutHandler() {
        //TODO: due to using reflection, the global timeout handler should be cached once it is
        // there, and used as long as the system property is still set?
        TimeoutHandler handler = null;
        String timeoutHandlerClassName = System.getProperty(Timeout.TIMEOUT_HANDLER_CLASS_NAME_PROPERTY_NAME);
        if (timeoutHandlerClassName != null) {
            try {
System.err.println("FOUND");
                Class<?> timeoutHandlerClass = Class.forName(timeoutHandlerClassName);
                Object handlerInstance = timeoutHandlerClass.newInstance();
                handler = TimeoutHandler.class.cast(handlerInstance);
            } catch (ClassNotFoundException e) {
                //TODO: or throw InitializationError?
                throw new RuntimeException(String.format("Failed to find timeout handler class '%s' specified via system property", timeoutHandlerClassName), e);
            } catch (InstantiationException e) {
                //TODO: or throw InitializationError?
                throw new RuntimeException(String.format("Failed to instantiate timeout handler class '%s'", timeoutHandlerClassName), e);
            } catch (IllegalAccessException e) {
                //TODO: or throw InitializationError?
                throw new RuntimeException(String.format("Failed to access timeout handler class '%s' during instantion", timeoutHandlerClassName), e);
            } catch (ClassCastException e) {
                //TODO: or throw InitializationError?
                throw new RuntimeException(String.format("Failed to cast timeout handler class '%s' to '%s'", timeoutHandlerClassName, TimeoutHandler.class), e);
            }
        }
        return handler;
    }

    /**
     * Retrieves the stack trace for a given thread.
     * @param thread The thread whose stack is to be retrieved.
     * @return The stack trace; returns a zero-length array if the thread has
     * terminated or the stack cannot be retrieved for some other reason.
     */
    private StackTraceElement[] getStackTrace(Thread thread) {
        try {
            return thread.getStackTrace();
        } catch (SecurityException e) {
            return new StackTraceElement[0];
        }
    }

    /**
     * Determines whether the test appears to be stuck in some thread other than
     * the "main thread" (the one created to run the test).  This feature is experimental.
     * Behavior may change after the 4.12 release in response to feedback.
     * @param mainThread The main thread created by {@code evaluate()}
     * @return The thread which appears to be causing the problem, if different from
     * {@code mainThread}, or {@code null} if the main thread appears to be the
     * problem or if the thread cannot be determined.  The return value is never equal
     * to {@code mainThread}.
     */
    private Thread getStuckThread (Thread mainThread) {
        if (fThreadGroup == null)
            return null;
        Thread[] threadsInGroup = getThreadArray(fThreadGroup);
        if (threadsInGroup == null)
            return null;

        // Now that we have all the threads in the test's thread group: Assume that
        // any thread we're "stuck" in is RUNNABLE.  Look for all RUNNABLE threads.
        // If just one, we return that (unless it equals threadMain).  If there's more
        // than one, pick the one that's using the most CPU time, if this feature is
        // supported.
        Thread stuckThread = null;
        long maxCpuTime = 0;
        for (Thread thread : threadsInGroup) {
            if (thread.getState() == Thread.State.RUNNABLE) {
                long threadCpuTime = cpuTime(thread);
                if (stuckThread == null || threadCpuTime > maxCpuTime) {
                    stuckThread = thread;
                    maxCpuTime = threadCpuTime;
                }
            }
        }
        return (stuckThread == mainThread) ? null : stuckThread;
    }

    /**
     * Returns all active threads belonging to a thread group.
     * @param group The thread group.
     * @return The active threads in the thread group.  The result should be a
     * complete list of the active threads at some point in time.  Returns {@code null}
     * if this cannot be determined, e.g. because new threads are being created at an
     * extremely fast rate.
     */
    private Thread[] getThreadArray(ThreadGroup group) {
        final int count = group.activeCount(); // this is just an estimate
        int enumSize = Math.max(count * 2, 100);
        int enumCount;
        Thread[] threads;
        int loopCount = 0;
        while (true) {
            threads = new Thread[enumSize];
            enumCount = group.enumerate(threads);
            if (enumCount < enumSize) break;
            // if there are too many threads to fit into the array, enumerate's result
            // is >= the array's length; therefore we can't trust that it returned all
            // the threads.  Try again.
            enumSize += 100;
            if (++loopCount >= 5)
                return null;
            // threads are proliferating too fast for us.  Bail before we get into
            // trouble.
        }
        return copyThreads(threads, enumCount);
    }

    /**
     * Returns an array of the first {@code count} Threads in {@code threads}.
     * (Use instead of Arrays.copyOf to maintain compatibility with Java 1.5.)
     * @param threads The source array.
     * @param count The maximum length of the result array.
     * @return The first {@count} (at most) elements of {@code threads}.
     */
    private Thread[] copyThreads(Thread[] threads, int count) {
        int length = Math.min(count, threads.length);
        Thread[] result = new Thread[length];
        for (int i = 0; i < length; i++)
            result[i] = threads[i];
        return result;
    }

    /**
     * Returns the CPU time used by a thread, if possible.
     * @param thr The thread to query.
     * @return The CPU time used by {@code thr}, or 0 if it cannot be determined.
     */
    private long cpuTime (Thread thr) {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        if (mxBean.isThreadCpuTimeSupported()) {
            try {
                return mxBean.getThreadCpuTime(thr.getId());
            } catch (UnsupportedOperationException e) {
            }
        }
        return 0;
    }

    private class CallableStatement implements Callable<Throwable> {
        public Throwable call() throws Exception {
            try {
                fOriginalStatement.evaluate();
            } catch (Exception e) {
                throw e;
            } catch (Throwable e) {
                return e;
            }
            return null;
        }
    }
}
