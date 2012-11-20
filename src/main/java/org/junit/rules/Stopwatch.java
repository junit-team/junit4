package org.junit.rules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;

import java.util.concurrent.TimeUnit;

/**
 * The Stopwatch Rule notifies one of own protected methods of the time spent by a test.<p/>
 * Override them to get the time in nanoseconds. For example, this class will keep logging the
 * time spent by each passing, failing and skipped test:
 *
 * <pre>
 * public static class StopwatchTest {
 *     private static final Logger logger = Logger.getLogger(&quot;&quot;);
 *
 *     private static void logInfo(String testName, String status, long nanos) {
 *         logger.info(String.format(&quot;Test %s %s, spent %d microseconds&quot;,
 *                                     testName, status, Stopwatch.toMicros(nanos)));
 *     }
 *
 *     &#064;Rule
 *     public Stopwatch stopwatch= new Stopwatch() {
 *         &#064;Override
 *         protected void succeeded(long nanos, Description description) {
 *             logInfo(description.getMethodName(), &quot;succeeded&quot;, nanos);
 *         }
 *
 *         &#064;Override
 *         protected void failed(long nanos, Throwable e, Description description) {
 *             logInfo(description.getMethodName(), &quot;failed&quot;, nanos);
 *         }
 *
 *         &#064;Override
 *         protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
 *             logInfo(description.getMethodName(), &quot;skipped&quot;, nanos);
 *         }
 *
 *         &#064;Override
 *         protected void finished(long nanos, Description description) {
 *             logInfo(description.getMethodName(), &quot;finished&quot;, nanos);
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
 * @author tibor17
 * @since 4.12
 */
public class Stopwatch extends TestWatcher {
    private long startNanos = 0L;
    private long endNanos = 0L;

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

    /**
     * @param nanos time in nanoseconds
     * @return time converted to microseconds
     */
    public static long toMicros(long nanos) {
        return TimeUnit.NANOSECONDS.toMicros(nanos);
    }

    /**
     * @param nanos time in nanoseconds
     * @return time converted to milliseconds
     */
    public static long toMillis(long nanos) {
        return TimeUnit.NANOSECONDS.toMillis(nanos);
    }

    /**
     * @param nanos time in nanoseconds
     * @return time converted to seconds
     */
    public static long toSeconds(long nanos) {
        return TimeUnit.NANOSECONDS.toSeconds(nanos);
    }

    private long getNanos() {
        return endNanos - startNanos;
    }

    private void starting() {
        startNanos = System.nanoTime();
    }

    private void stopping() {
        endNanos = System.nanoTime();
    }

    @Override final protected void succeeded(Description description) {
        stopping();
        succeeded(getNanos(), description);
    }

    @Override final protected void failed(Throwable e, Description description) {
        stopping();
        failed(getNanos(), e, description);
    }

    @Override final protected void skipped(AssumptionViolatedException e, Description description) {
        stopping();
        skipped(getNanos(), e, description);
    }

    @Override final protected void starting(Description description) {
        starting();
    }

    @Override final protected void finished(Description description) {
        finished(getNanos(), description);
    }
}
