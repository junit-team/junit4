package org.junit.experimental.parallel;

import org.junit.runner.Description;
import org.junit.runners.model.RunnerScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * Schedules tests and controls shared thread resources.
 * <p>
 * The scheduler objects should be first created (and wired if share common threads) and set in runners
 * {@link org.junit.runners.ParentRunner#setScheduler(org.junit.runners.model.RunnerScheduler)}.
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class Scheduler implements RunnerScheduler {
    private final Balancer balancer;
    private final SchedulingStrategy strategy;
    private final Set<Controller> slaves = new CopyOnWriteArraySet<Controller>();
    private final Description description;
    private volatile boolean shutdown = false;
    private volatile boolean started = false;
    private volatile Controller masterController;

    /**
     * Use e.g. parallel classes have own non-shared thread pool, and methods another pool.
     * <p>
     * You can use it with one infinite thread pool shared in strategies across all
     * suites, class runners, etc.
     */
    public Scheduler(Description description, SchedulingStrategy strategy) {
        this(description, strategy, -1);
    }

    /**
     * Should be used if schedulers in parallel children and this use one instance of bounded thread pool.
     * <p>
     * Set this scheduler in a e.g. one suite of classes, and every individual class runner should
     * reference {@link #Scheduler(org.junit.runner.Description, Scheduler, SchedulingStrategy)}
     * or {@link #Scheduler(org.junit.runner.Description, Scheduler, SchedulingStrategy, int)}.
     *
     * @param description description of current runner
     * @param strategy scheduling strategy with a shared thread pool used only by this scheduler
     * @param concurrency determines maximum concurrent children scheduled a time via {@link #schedule(Runnable)}
     * @throws IllegalArgumentException if <tt>scheduler</tt> does not share own thread resources
     * @throws NullPointerException if null <tt>strategy</tt>
     */
    public Scheduler(Description description, SchedulingStrategy strategy, int concurrency) {
        if (strategy == null) {
            throw new NullPointerException("null strategy");
        }
        if (!strategy.hasSharedThreadPool() && concurrency > 0) {
            throw new IllegalArgumentException("expects scheduling strategy with a shared thread pool");
        }
        strategy.setDefaultShutdownHandler(new ShutdownHandler());
        this.description = description;
        this.strategy = strategy;
        this.balancer = createBalancer(concurrency);
        masterController = null;
    }

    /**
     * @param masterScheduler a reference to {@link #Scheduler(org.junit.runner.Description, SchedulingStrategy, int)}
     *                        or {@link #Scheduler(org.junit.runner.Description, SchedulingStrategy)}
     * @see #Scheduler(org.junit.runner.Description, SchedulingStrategy)
     * @see #Scheduler(org.junit.runner.Description, SchedulingStrategy, int)
     */
    public Scheduler(Description description, Scheduler masterScheduler, SchedulingStrategy strategy, int concurrency) {
        this(description, strategy, concurrency);
        if (!masterScheduler.strategy.hasSharedThreadPool()) {
            throw new IllegalArgumentException("the scheduler does not share threads");
        }
        strategy.setDefaultShutdownHandler(new ShutdownHandler());
        masterScheduler.shareWith(this);
    }

    /**
     * Should be used only with infinite thread pool, otherwise would not prevent from thread resources exhaustion.
     *
     * @see #Scheduler(org.junit.runner.Description, Scheduler, SchedulingStrategy, int)
     */
    public Scheduler(Description description, Scheduler masterScheduler, SchedulingStrategy strategy) {
        this(description, masterScheduler, strategy, -1);
    }

    private static Balancer createBalancer(int concurrency) {
        return concurrency <= 0 ? new Balancer() : new Balancer(concurrency);
    }

    private void setController(Controller masterController) {
        if (masterController == null) {
            throw new NullPointerException("null ExecutionController");
        }
        this.masterController = masterController;
    }

    /**
     * @param slave a slave scheduler to register
     * @return <tt>true</tt> if successfully registered the <tt>slave</tt> using
     * <em>shared</em> thread resources in this scheduler
     */
    private boolean shareWith(Scheduler slave) {
        boolean canRegister = slave != null && slave != this
                && strategy.hasSharedThreadPool() && slave.strategy.hasSharedThreadPool();

        if (canRegister) {
            Controller controller = new Controller(slave);
            canRegister = !slaves.contains(controller);
            if (canRegister) {
                slaves.add(controller);
                slave.setController(controller);
            }
        }

        return canRegister;
    }

    /**
     * @return <tt>true</tt> if new tasks can be scheduled.
     */
    private boolean canSchedule() {
        return !shutdown && (masterController == null || masterController.canSchedule());
    }

    protected void logQuietly(Throwable t) {
        t.printStackTrace(System.err);
    }

    protected void logQuietly(String msg) {
        System.err.println(msg);
    }

    /**
     * Attempts to stop all actively executing tasks and immediately returns a collection
     * of descriptions of those tasks which have completed prior to this call.
     * <p>
     * If has a strategy with shared thread pool, the children will shutdown as well.
     * If <tt>shutdownNow</tt> is set, waiting methods will cancel via {@link Thread#interrupt}.
     *
     * @param shutdownNow if <tt>true</tt> interrupts waiting methods
     * @return collection of descriptions started before shutting down
     */
    public Collection<Description> shutdown(boolean shutdownNow) {
        shutdown = true;
        ArrayList<Description> activeChildren = new ArrayList<Description>();

        if (started && description != null) {
            activeChildren.add(description);
        }

        if (strategy.hasSharedThreadPool()) {
            for (Controller slave : slaves) {
                try {
                    activeChildren.addAll(slave.shutdown(shutdownNow));
                } catch (Throwable t) {
                    logQuietly(t);
                }
            }
        }

        try {
            balancer.releaseAllPermits();
        } finally {
            strategy.stopNow();
        }

        return activeChildren;
    }

    protected void beforeExecute() {
    }

    protected void afterExecute() {
    }

    public void schedule(Runnable childStatement) {
        if (childStatement == null) {
            logQuietly("cannot schedule null");
        } else if (canSchedule() && strategy.canSchedule()) {
            try {
                strategy.schedule(wrapTask(childStatement));
                started = true;
            } catch (RejectedExecutionException e) {
                shutdown(false);
            } catch (Throwable t) {
                logQuietly(t);
            }
        }
    }

    public void finished() {
        try {
            strategy.finished();
        } catch (InterruptedException e) {
            logQuietly(e);
        } finally {
            for (Controller slave : slaves) {
                slave.awaitFinishedQuietly();
            }
        }
    }

    private Runnable wrapTask(final Runnable task) {
        return new Runnable() {
            public void run() {
                try {
                    balancer.acquirePermit();
                    beforeExecute();
                    task.run();
                } finally {
                    try {
                        afterExecute();
                    } finally {
                        balancer.releasePermit();
                    }
                }
            }
        };
    }

    /**
     * Used if has scheduling strategy with a shared thread pool.
     */
    private final class Controller {
        private final Scheduler slave;

        private Controller(Scheduler slave) {
            this.slave = slave;
        }

        /**
         * @return <tt>true</tt> if new children can be scheduled.
         */
        public boolean canSchedule() {
            return Scheduler.this.canSchedule();
        }

        public void awaitFinishedQuietly() {
            try {
                slave.finished();
            } catch(Throwable t) {
                slave.logQuietly(t);
            }
        }

        Collection<Description> shutdown(boolean shutdownNow) {
            return slave.shutdown(shutdownNow);
        }

        @Override
        public int hashCode() {
            return slave.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof Controller) && slave.equals(((Controller) o).slave);
        }
    }

    public final class ShutdownHandler implements RejectedExecutionHandler {
        private volatile RejectedExecutionHandler poolHandler;

        private ShutdownHandler() {
            poolHandler = null;
        }

        void setRejectedExecutionHandler(RejectedExecutionHandler poolHandler) {
            this.poolHandler = poolHandler;
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                shutdown(false);
            }
            final RejectedExecutionHandler poolHandler = this.poolHandler;
            if (poolHandler != null) {
                poolHandler.rejectedExecution(r, executor);
            }
        }
    }
}
