package org.junit.tests.experimental.parallel;

import org.junit.Test;
import org.junit.experimental.parallel.SchedulingStrategies;
import org.junit.experimental.parallel.SchedulingStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the factories in SchedulingStrategy.
 *
 * Th point of these tests is to check {@link Task#result} if changed
 * from <code>false</code> to <code>true</code> after all scheduled tasks
 * have finished.
 * The call {@link SchedulingStrategy#finished()} is waiting until the
 * strategy has finished.
 * Then {@link Task#result} should be asserted that is <code>true</code>.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 *
 * @see SchedulingStrategy
 */
public class SchedulingStrategiesTest {
    static class Task implements Runnable {
        volatile boolean result = false;

        public void run() {
            result = true;
        }
    }

    @Test
    public void invokerStrategy() throws InterruptedException {
        SchedulingStrategy strategy = SchedulingStrategies.createInvokerStrategy();
        assertFalse(strategy.hasSharedThreadPool());
        assertTrue(strategy.canSchedule());

        Task task = new Task();

        strategy.schedule(task);

        assertTrue(strategy.canSchedule());

        assertTrue(task.result);

        assertTrue(strategy.finished());
        assertFalse(strategy.canSchedule());
    }

    @Test
    public void nonSharedPoolStrategy() throws InterruptedException {
        SchedulingStrategy strategy = SchedulingStrategies.createParallelStrategy(2);
        assertFalse(strategy.hasSharedThreadPool());
        assertTrue(strategy.canSchedule());

        Task task1 = new Task();
        Task task2 = new Task();

        strategy.schedule(task1);
        strategy.schedule(task2);

        assertTrue(strategy.canSchedule());

        assertTrue(strategy.finished());
        assertFalse(strategy.canSchedule());

        assertTrue(task1.result);
        assertTrue(task2.result);
    }

    @Test(expected = NullPointerException.class)
    public void sharedPoolStrategyNullPool() {
        SchedulingStrategies.createParallelSharedStrategy(null);
    }

    @Test
    public void sharedPoolStrategy() throws InterruptedException {
        ExecutorService sharedPool = Executors.newCachedThreadPool();

        SchedulingStrategy strategy1 = SchedulingStrategies.createParallelSharedStrategy(sharedPool);
        assertTrue(strategy1.hasSharedThreadPool());
        assertTrue(strategy1.canSchedule());

        SchedulingStrategy strategy2 = SchedulingStrategies.createParallelSharedStrategy(sharedPool);
        assertTrue(strategy2.hasSharedThreadPool());
        assertTrue(strategy2.canSchedule());

        Task task1 = new Task();
        Task task2 = new Task();
        Task task3 = new Task();
        Task task4 = new Task();

        strategy1.schedule(task1);
        strategy2.schedule(task2);
        strategy1.schedule(task3);
        strategy2.schedule(task4);

        assertTrue(strategy1.canSchedule());
        assertTrue(strategy2.canSchedule());

        assertTrue(strategy1.finished());
        assertFalse(strategy1.canSchedule());

        assertTrue(strategy2.finished());
        assertFalse(strategy2.canSchedule());

        assertTrue(task1.result);
        assertTrue(task2.result);
        assertTrue(task3.result);
        assertTrue(task4.result);
    }

    @Test
    public void infinitePoolStrategy() throws InterruptedException {
        SchedulingStrategy strategy = SchedulingStrategies.createParallelStrategyUnbounded();
        assertFalse(strategy.hasSharedThreadPool());
        assertTrue(strategy.canSchedule());

        Task task1 = new Task();
        Task task2 = new Task();

        strategy.schedule(task1);
        strategy.schedule(task2);

        assertTrue(strategy.canSchedule());

        assertTrue(strategy.finished());
        assertFalse(strategy.canSchedule());

        assertTrue(task1.result);
        assertTrue(task2.result);
    }
}
