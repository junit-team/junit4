package org.junit.experimental.parallel;

import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Specifies the strategy of scheduling whether sequential, or parallel.
 * The strategy may use a thread pool <em>shared</em> with other strategies.
 * <p/>
 * This instance of strategy is consumed by one executor {@link AbstractExecutor}.
 * <p/>
 * The strategy has methods to schedule tasks and await them to complete.
 * Methods {@link #schedule(Runnable)} and {@link #finished()} should be used
 * in the same thread.
 *
 * @author Tibor Digana (tibor17)
 * @version 4.12
 * @since 4.12
 */
public abstract class SchedulingStrategy {

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
     * waiting for other strategies to finish. The {@link RunnerScheduler#finished()} may
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

    /**
     * Schedules tasks if {@link #canSchedule()}.
     *
     * @param task runnable to schedule in a thread pool or invoke
     * @throws RejectedExecutionException if <tt>task</tt>
     *         cannot be scheduled for execution
     * @throws NullPointerException if <tt>task</tt> is <tt>null</tt>
     * @see RunnerScheduler#schedule(Runnable)
     * @see java.util.concurrent.Executor#execute(Runnable)
     */
    public abstract void schedule(Runnable task);

    /**
     * Waiting for scheduled tasks to finish.
     * New tasks will not be scheduled by calling this method.
     *
     * @return <tt>true</tt> if successfully stopped the scheduler, else
     *         <tt>false</tt> if already stopped (a <em>shared</em> thread
     *         pool was shutdown externally).
     * @throws InterruptedException if interrupted while waiting
     *         for scheduled tasks to finish
     * @see RunnerScheduler#finished()
     */
    public boolean finished() throws InterruptedException {
        boolean wasRunning = stop();
        awaitStopped();
        return wasRunning;
    }

    /**
     * Stops scheduling new tasks (e.g. by {@link ExecutorService#shutdown()}
     * on a private thread pool which cannot be <em>shared</em> with other strategy).
     *
     * @return <tt>true</tt> if successfully stopped the scheduler, else
     *         <tt>false</tt> if already stopped (a <em>shared</em> thread
     *         pool was shutdown externally).
     * @see ExecutorService#shutdown()
     */
    protected abstract boolean stop();

    /**
     * Stops scheduling new tasks and <em>interrupts</em> running tasks
     * (e.g. by {@link ExecutorService#shutdownNow()} on a private thread pool
     * which cannot be <em>shared</em> with other strategy).
     * <p>
     * This method calls {@link #stop()} by default.
     *
     * @return <tt>true</tt> if successfully stopped the scheduler, else
     *         <tt>false</tt> if already stopped (a <em>shared</em> thread
     *         pool was shutdown externally).
     * @see ExecutorService#shutdownNow()
     */
    protected boolean stopNow() {
        return stop();
    }

    /**
     * Blocks until all tasks have completed execution after a stop
     * request, or the current thread is interrupted. Returns immediately
     * if already stopped.
     *
     * @throws InterruptedException if interrupted while waiting
     */
    protected abstract void awaitStopped() throws InterruptedException;

    /**
     * @return <tt>true</tt> if a thread pool associated with this strategy
     * can be shared with other strategies.
     */
    public abstract boolean hasSharedThreadPool();

    /**
     * @return <tt>true</tt> unless stopped or finished.
     */
    public abstract boolean canSchedule();
}
