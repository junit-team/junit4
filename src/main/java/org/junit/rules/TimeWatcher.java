package org.junit.rules;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;

/**
 * The TimeWatcher Rule notifies one of own protected methods of the time spent by a test.<p/>
 * Override them to get the time in nanoseconds. For example, this class will keep logging the
 * time spent by each passing, failing and skipped test:
 *
 * <pre>
 * public static class TimeWatcherTest {
 *  private static final Logger logger = Logger.getLogger("");
 *
 *  private static void logInfo(String testName, String status, long nanos) {
 *      logger.info(String.format(&quot;Test %s %s, spent %d microseconds&quot;,
 *      testName, status, TimeWatcher.micros(nanos)));
 *  }
 *
 *  &#064;Rule
 *  public TimeWatcher timeWatcher= new TimeWatcher() {
 *      &#064;Override
 *      protected void succeeded(long nanos, Description description) {
 *          logInfo(description.getMethodName(), &quot;succeeded&quot;, nanos);
 *      }
 *
 *      &#064;Override
 *      protected void failed(long nanos, Throwable e, Description description) {
 *          logInfo(description.getMethodName(), &quot;failed&quot;, nanos);
 *      }
 *
 *      &#064;Override
 *      protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
 *          logInfo(description.getMethodName(), &quot;skipped&quot;, nanos);
 *      }
 *  };
 *
 *  &#064;Test
 *  public void succeeds() {
 *  }
 *
 *  &#064;Test
 *  public void fails() {
 *      fail();
 *  }
 *
 *  &#064;Test
 *  public void skips() {
 *      assumeTrue(false);
 *  }
 * }
 * </pre>
 *
 * @author tibor17
 * @since 4.12
 */
public class TimeWatcher extends TestWatcher {
    private volatile long startTime = 0L;
    private volatile long endTime = 0L;

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
     * @param nanos time in nanoseconds
     * @return time converted to microseconds
     */
    public static long micros(long nanos) {
        return nanos / (long) 1E3;
    }

    /**
     * @param nanos time in nanoseconds
     * @return time converted to milliseconds
     */
    public static long millis(long nanos) {
        return nanos / (long) 1E6;
    }

    /**
     * @param nanos time in nanoseconds
     * @return time converted to seconds
     */
    public static long seconds(long nanos) {
        return nanos / (long) 1E9;
    }

    private long getNanos() {
        return endTime - startTime;
    }

    @Override final protected void succeeded(Description description) {
        endTime = System.nanoTime();
        succeeded(getNanos(), description);
    }

    @Override final protected void failed(Throwable e, Description description) {
        endTime = System.nanoTime();
        failed(getNanos(), e, description);
    }

    @Override final protected void skipped(AssumptionViolatedException e, Description description) {
        endTime = System.nanoTime();
        skipped(getNanos(), e, description);
    }

    @Override final protected void starting(Description description) {
        startTime = System.nanoTime();
    }

    @Override final protected void finished(Description description) {
    }
}
