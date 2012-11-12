package org.junit.rules;

import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeUnit;

/**
 * The Timeout Rule applies the same timeout to all test methods in a class:
 *
 * <pre>
 * public static class HasGlobalLongTimeout {
 *
 *  &#064;Rule
 *  public Timeout globalTimeout= new Timeout(20);
 *
 *  &#064;Test
 *  public void run1() throws InterruptedException {
 *      Thread.sleep(100);
 *  }
 *
 *  &#064;Test
 *  public void run2() throws InterruptedException {
 *      reentrantLock.lockInterruptibly();
 *      try {
 *         ...
 *      } finally {
 *         reentrantLock.unlock();
 *      }
 *  }
 *
 *  &#064;Test
 *  public synchronized void run3() throws InterruptedException {
 *      ...
 *      wait();
 *  }
 * }
 * </pre>
 *
 * A test is running in an extra thread, and its execution is interrupted via {@link Thread#interrupt}
 * when the timeout elapsed. This happens in interruptable I/O and locks, and methods in {@link Object}
 * and {@link Thread} throwing {@link InterruptedException}.
 *
 * @since 4.7
 */
public class Timeout implements TestRule {
    private final long fTimeout;
    private final TimeUnit fTimeUnit;

    /**
     * Create a {@code Timeout} instance with the timeout specified
     * in milliseconds.
     * This constructor is deprecated and will be removed in the next release.
     * Instead use {@link #Timeout(long)} or
     * {@link #Timeout(long, java.util.concurrent.TimeUnit)}.
     *
     * @param millis the maximum time in milliseconds to allow the
     * test to run before it should timeout
     */
    @Deprecated
    public Timeout(int millis) {
        this((long) millis);
    }

    /**
     * @param millis the millisecond timeout
     * @since 4.11
     */
    public Timeout(long millis) {
        this(millis, TimeUnit.MILLISECONDS);
    }

    /**
     * Create a {@code Timeout} instance with the timeout specified
     * at the unit of granularity of the provided {@code TimeUnit}.
     *
     * @param timeout the maximum time to allow the test to run
     * before it should timeout
     * @param unit the time unit for the {@code timeout}
     * @since 4.11
     */
    public Timeout(long timeout, TimeUnit unit) {
        fTimeout = timeout;
        fTimeUnit = unit;
    }

    public Statement apply(Statement base, Description description) {
        return new FailOnTimeout(base, fTimeout, fTimeUnit);
    }
}