package org.junit.rules;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.concurrent.TimeUnit;

/**
 * The Stopwatch Rule notifies one of its own protected methods of the time spent by a test.
 *
 * <p>Override them to get the time in nanoseconds. For example, this class will keep logging the
 * time spent by each passed, failed, skipped, and finished test:
 *
 * <pre>
 * public static class StopwatchTest {
 *     private static final Logger logger = Logger.getLogger(&quot;&quot;);
 *
 *     private static void logInfo(Description description, String status, long nanos) {
 *         String testName = description.getMethodName();
 *         logger.info(String.format(&quot;Test %s %s, spent %d microseconds&quot;,
 *                                   testName, status, TimeUnit.NANOSECONDS.toMicros(nanos)));
 *     }
 *
 *     &#064;Rule
 *     public Stopwatch stopwatch = new Stopwatch() {
 *         &#064;Override
 *         protected void succeeded(long nanos, Description description) {
 *             logInfo(description, &quot;succeeded&quot;, nanos);
 *         }
 *
 *         &#064;Override
 *         protected void failed(long nanos, Throwable e, Description description) {
 *             logInfo(description, &quot;failed&quot;, nanos);
 *         }
 *
 *         &#064;Override
 *         protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
 *             logInfo(description, &quot;skipped&quot;, nanos);
 *         }
 *
 *         &#064;Override
 *         protected void finished(long nanos, Description description) {
 *             logInfo(description, &quot;finished&quot;, nanos);
 *         }
 *     };
 *
 *     &#064;Test
 *     public void succeeds() {
 *     }
 *
 *     &#064;Test
 *     public void fails() {
 *         fail();
 *     }
 *
 *     &#064;Test
 *     public void skips() {
 *         assumeTrue(false);
 *     }
 * }
 * </pre>
 *
 * An example to assert runtime:
 * <pre>
 * &#064;Test
 * public void performanceTest() throws InterruptedException {
 *     long delta = 30;
 *     Thread.sleep(300L);
 *     assertEquals(300d, stopwatch.runtime(MILLISECONDS), delta);
 *     Thread.sleep(500L);
 *     assertEquals(800d, stopwatch.runtime(MILLISECONDS), delta);
 * }
 * </pre>
 *
 * @author tibor17
 * @since 4.12
 */
public abstract class Stopwatch implements TestRule {
    private final Clock clock;
    private volatile long startNanos;
    private volatile long endNanos;

    public Stopwatch() {
        this(new Clock());
    }

    Stopwatch(Clock clock) {
        this.clock = clock;
    }

    /**
     * Gets the runtime for the test.
     *
     * @param unit time unit for returned runtime
     * @return runtime measured during the test
     */
    public long runtime(TimeUnit unit) {
        return unit.convert(getNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Invoked when a test succeeds
     */
    protected void succeeded(long nanos, Description description) {
    }

    /**
     * Invoked when a test fails
     */
    protected void failed(long nanos, Throwable e, Description description) {
    }

    /**
     * Invoked when a test is skipped due to a failed assumption.
     */
    protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
    }

    /**
     * Invoked when a test method finishes (whether passing or failing)
     */
    protected void finished(long nanos, Description description) {
    }

    private long getNanos() {
        if (startNanos == 0) {
            throw new IllegalStateException("Test has not started");
        }
        long currentEndNanos = endNanos; // volatile read happens here
        if (currentEndNanos == 0) {
          currentEndNanos = clock.nanoTime();
        }

        return currentEndNanos - startNanos;
    }

    private void starting() {
        startNanos = clock.nanoTime();
        endNanos = 0;
    }

    private void stopping() {
        endNanos = clock.nanoTime();
    }

    public final Statement apply(Statement base, Description description) {
        return new InternalWatcher().apply(base, description);
    }

    private class InternalWatcher extends TestWatcher {

        @Override protected void starting(Description description) {
            Stopwatch.this.starting();
        }

        @Override protected void finished(Description description) {
            Stopwatch.this.finished(getNanos(), description);
        }

        @Override protected void succeeded(Description description) {
            Stopwatch.this.stopping();
            Stopwatch.this.succeeded(getNanos(), description);
        }

        @Override protected void failed(Throwable e, Description description) {
            Stopwatch.this.stopping();
            Stopwatch.this.failed(getNanos(), e, description);
        }

        @Override protected void skipped(AssumptionViolatedException e, Description description) {
            Stopwatch.this.stopping();
            Stopwatch.this.skipped(getNanos(), e, description);
        }
    }

    static class Clock {

        public long nanoTime() {
            return System.nanoTime();
        }
    }
}
