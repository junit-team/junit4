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
 * Schedules tests, controls thread resources, awaiting tests and other schedulers finished, and
 * a master scheduler can shutdown slaves.
 * <p>
 * The scheduler objects should be first created (and wired) and set in runners
 * {@link org.junit.runners.ParentRunner#setScheduler(org.junit.runners.model.RunnerScheduler)}.
 * <p>
 * A new instance of scheduling strategy should be passed to the constructor of this scheduler.
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
     * Should be used if schedulers in parallel children and parent use one instance of bounded thread pool.
     * <p>
     * Set this scheduler in a e.g. one suite of classes, then every individual class runner should reference
     * {@link #Scheduler(org.junit.runner.Description, Scheduler, SchedulingStrategy)}
     * or {@link #Scheduler(org.junit.runner.Description, Scheduler, SchedulingStrategy, int)}.
     *
     * @param description description of current runner
     * @param strategy scheduling strategy with a shared thread pool
     * @param concurrency determines maximum concurrent children scheduled a time via {@link #schedule(Runnable)}
     * @throws NullPointerException if null <tt>strategy</tt>
     */
    public Scheduler(Description description, SchedulingStrategy strategy, int concurrency) {
        this(description, strategy, new Balancer(concurrency));
    }

    /**
     * New instances should be used by schedulers with limited concurrency by <tt>balancer</tt>
     * against other groups of schedulers. The schedulers share one pool.
     * <p>
     * Unlike in {@link #Scheduler(org.junit.runner.Description, SchedulingStrategy, int)} which was limiting
     * the <tt>concurrency</tt> of children of a runner where this scheduler was set, <em>this</em> <tt>balancer</tt>
     * is limiting the concurrency of all children in runners having schedulers created by this constructor.
     *
     * @param description description of current runner
     * @param strategy scheduling strategy which may share threads with other strategy
     * @param balancer determines maximum concurrent children scheduled a time via {@link #schedule(Runnable)}
     * @throws NullPointerException if null <tt>strategy</tt> or <tt>balancer</tt>
     */
    public Scheduler(Description description, SchedulingStrategy strategy, Balancer balancer) {
        strategy.setDefaultShutdownHandler(newShutdownHandler());
        this.description = description;
        this.strategy = strategy;
        this.balancer = balancer;
        masterController = null;
    }
    /**
     * Can be used by e.g. a runner having parallel classes in use case with parallel
     * suites, classes and methods sharing the same thread pool.
     *
     * @param description description of current runner
     * @param masterScheduler scheduler sharing own threads with this slave
     * @param strategy scheduling strategy for this scheduler
     * @param balancer determines maximum concurrent children scheduled a time via {@link #schedule(Runnable)}
     * @throws NullPointerException if null <tt>masterScheduler</tt>, <tt>strategy</tt> or <tt>balancer</tt>
     */
    public Scheduler(Description description, Scheduler masterScheduler, SchedulingStrategy strategy, Balancer balancer) {
        this(description, strategy, balancer);
        strategy.setDefaultShutdownHandler(newShutdownHandler());
        masterScheduler.register(this);
    }

    /**
     * @param masterScheduler a reference to {@link #Scheduler(org.junit.runner.Description, SchedulingStrategy, int)}
     *                        or {@link #Scheduler(org.junit.runner.Description, SchedulingStrategy)}
     * @see #Scheduler(org.junit.runner.Description, SchedulingStrategy)
     * @see #Scheduler(org.junit.runner.Description, SchedulingStrategy, int)
     */
    public Scheduler(Description description, Scheduler masterScheduler, SchedulingStrategy strategy, int concurrency) {
        this(description, strategy, concurrency);
        strategy.setDefaultShutdownHandler(newShutdownHandler());
        masterScheduler.register(this);
    }

    /**
     * Should be used with individual pools on suites, classes and methods, see
     * {@link org.junit.experimental.parallel.ParallelComputerBuilder#useSeparatePools()}.
     * <p>
     * Cached thread pool is infinite and can be always shared.
     */
    public Scheduler(Description description, Scheduler masterScheduler, SchedulingStrategy strategy) {
        this(description, masterScheduler, strategy, 0);
    }

    private void setController(Controller masterController) {
        if (masterController == null) {
            throw new NullPointerException("null ExecutionController");
        }
        this.masterController = masterController;
    }

    /**
     * @param slave a slave scheduler to register
     * @return <tt>true</tt> if successfully registered the <tt>slave</tt>.
     */
    private boolean register(Scheduler slave) {
        boolean canRegister = slave != null && slave != this;
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
     * of descriptions of those tasks which have started prior to this call.
     * <p>
     * This scheduler and other registered schedulers will shutdown, see {@link #register(Scheduler)}.
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

        for (Controller slave : slaves) {
            try {
                activeChildren.addAll(slave.shutdown(shutdownNow));
            } catch (Throwable t) {
                logQuietly(t);
            }
        }

        try {
            balancer.releaseAllPermits();
        } finally {
            if (shutdownNow) {
                strategy.stopNow();
            } else {
                strategy.stop();
            }
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
                balancer.acquirePermit();
                Runnable task = wrapTask(childStatement);
                strategy.schedule(task);
                started = true;
            } catch (RejectedExecutionException e) {
                shutdown(false);
            } catch (Throwable t) {
                balancer.releasePermit();
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

    protected ShutdownHandler newShutdownHandler() {
        return new ShutdownHandler();
    }

    /**
     * If this is a master scheduler, the slaves can stop scheduling by the master through the controller.
     */
    private final class Controller {
        private final Scheduler slave;

        private Controller(Scheduler slave) {
            this.slave = slave;
        }

        /**
         * @return <tt>true</tt> if new children can be scheduled.
         */
        boolean canSchedule() {
            return Scheduler.this.canSchedule();
        }

        void awaitFinishedQuietly() {
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

    public class ShutdownHandler implements RejectedExecutionHandler {
        private volatile RejectedExecutionHandler poolHandler;

        protected ShutdownHandler() {
            poolHandler = null;
        }

        public void setRejectedExecutionHandler(RejectedExecutionHandler poolHandler) {
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
