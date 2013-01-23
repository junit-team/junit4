package org.junit.experimental.parallel;

import java.util.concurrent.Executors;

/**
 * The factory of {@link SchedulingStrategy}.
 *
 * @author tibor17
 * @version 4.12
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
}
