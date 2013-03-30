package org.junit.experimental.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The factory of {@link SchedulingStrategy}.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class SchedulingStrategies {

    /**
     * @return sequentially executing strategy
     */
    public static SchedulingStrategy createInvokerStrategy() {
        return new InvokerStrategy();
    }

    /**
     * @param nThreads fixed pool capacity
     * @return parallel scheduling strategy
     */
    public static SchedulingStrategy createParallelStrategy(int nThreads) {
        return new NonSharedThreadPoolStrategy(Executors.newFixedThreadPool(nThreads));
    }

    /**
     * @return parallel scheduling strategy with unbounded capacity
     */
    public static SchedulingStrategy createParallelStrategyUnbounded() {
        return new NonSharedThreadPoolStrategy(Executors.newCachedThreadPool());
    }

    /**
     * The <tt>threadPool</tt> passed to this strategy can be shared in other strategies.
     * <p>
     * The call {@link SchedulingStrategy#finished()} is waiting until own tasks have finished.
     * New tasks will not be scheduled by this call in this strategy. This strategy is not
     * waiting for other strategies to finish. The {@link org.junit.runners.model.RunnerScheduler#finished()} may
     * freely use {@link SchedulingStrategy#finished()}.
     *
     * @param threadPool thread pool possibly shared with other strategies
     * @return parallel strategy with shared thread pool
     * @throws NullPointerException if <tt>threadPool</tt> is null
     */
    public static SchedulingStrategy createParallelSharedStrategy(ExecutorService threadPool) {
        if (threadPool == null) {
            throw new NullPointerException("null threadPool in #createParallelSharedStrategy");
        }
        return new SharedThreadPoolStrategy(threadPool);
    }
}
