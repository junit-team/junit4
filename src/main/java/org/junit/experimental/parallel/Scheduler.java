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
     * You can use it if runners with suites, classes and methods use the same shared pool.
     * Since the runner with parallel methods is the last in the chain, it's scheduler must
     * not have the balancer and uses this constructor.
     */
    public Scheduler(Description description, SchedulingStrategy strategy) {
        this(description, strategy, -1);
    }

    /**
     * The <tt>concurrency</tt> determines permits in {@link Balancer}.
     *
     * @see {@link #Scheduler(org.junit.runner.Description, SchedulingStrategy, Balancer)}
     */
    public Scheduler(Description description, SchedulingStrategy strategy, int concurrency) {
        this(description, strategy, createBalancer(concurrency));
    }

    /**
     * Should be used if a child scheduler shares threads reused by given <tt>strategy</tt>.
     * Can be used by e.g. suite runner having parallel classes in use case with parallel
     * classes and methods sharing the same thread pool.
     *
     * @param description description of current runner
     * @param strategy shared scheduling strategy
     * @param balancer determines maximum concurrent children scheduled a time via {@link #schedule(Runnable)}
     * @throws IllegalArgumentException if <tt>scheduler</tt> does not share own thread resources
     */
    public Scheduler(Description description, SchedulingStrategy strategy, Balancer balancer) {
        if (!strategy.hasSharedThreadPool() && balancer.getInitialPermits() > 0) {
            throw new IllegalArgumentException("expects shared scheduling strategy");
        }
        strategy.setDefaultShutdownHandler(new ShutdownHandler());
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
     * @throws IllegalArgumentException if <tt>scheduler</tt> does not share thread resources
     */
    public Scheduler(Description description, Scheduler masterScheduler, SchedulingStrategy strategy, Balancer balancer) {
        this(description, strategy, balancer);
        if (!masterScheduler.hasSharedStrategyPool()) {
            throw new IllegalArgumentException("the scheduler does not share threads");
        }
        strategy.setDefaultShutdownHandler(new ShutdownHandler());
        masterScheduler.shareWith(this);
    }

    /**
     * Can be used by e.g. a runner having parallel methods in use case with parallel
     * classes and methods sharing the same thread pool.
     *
     * @param description description of current runner
     * @param masterScheduler scheduler sharing own threads with this slave
     * @param strategy scheduling strategy for this scheduler
     * @throws IllegalArgumentException if <tt>scheduler</tt> does not share threads
     */
    public Scheduler(Description description, Scheduler masterScheduler, SchedulingStrategy strategy) {
        this(description, masterScheduler, strategy, createBalancer(-1));
    }

    private static Balancer createBalancer(int concurrency) {
        return concurrency <= 0 ? new Balancer() : new Balancer(concurrency);
    }

    void setController(Controller masterController) {
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
    protected boolean shareWith(Scheduler slave) {
        final boolean canRegister = slave != null && slave != this
                && hasSharedStrategyPool() && slave.hasSharedStrategyPool();

        if (canRegister) {
            Controller controller = new Controller(slave);
            if (!slaves.contains(controller)) {
                slaves.add(controller);
                slave.setController(controller);
            }
        }

        return canRegister;
    }

    protected Balancer getBalancer() {
        return balancer;
    }

    public boolean hasSharedStrategyPool() {
        SchedulingStrategy strategy = getSchedulingStrategy();
        return strategy != null && strategy.hasSharedThreadPool();
    }

    /**
     * @return <tt>true</tt> if new tasks can be scheduled.
     */
    protected boolean canSchedule() {
        return !shutdown && (masterController == null || masterController.canSchedule());
    }

    protected SchedulingStrategy getSchedulingStrategy() {
        return strategy;
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
     * If {@link #hasSharedStrategyPool()}, the children will shutdown as well.
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

        if (hasSharedStrategyPool()) {
            for (Controller slave : slaves) {
                try {
                    activeChildren.addAll(slave.shutdown(shutdownNow));
                } catch (Throwable t) {
                    logQuietly(t);
                }
            }
        }

        try {
            getBalancer().releaseAllPermits();
        } finally {
            getSchedulingStrategy().stopNow();
        }

        return activeChildren;
    }

    protected void beforeScheduling() {
    }

    /**
     * @param scheduled <tt>true</tt> if was not rejected
     */
    protected void afterScheduling(boolean scheduled) {
    }

    protected boolean trySchedule(Runnable childStatement) {
        final SchedulingStrategy strategy = getSchedulingStrategy();
        boolean scheduled = canSchedule() && strategy != null && strategy.canSchedule();
        if (scheduled) {
            try {
                beforeScheduling();
                strategy.schedule(childStatement);
            } catch (RejectedExecutionException e) {
                shutdown(false);
                scheduled = false;
            } finally {
                started |= scheduled;
                afterScheduling(scheduled);
            }
        }
        return scheduled;
    }

    public void schedule(Runnable childStatement) {
        if (childStatement == null) {
            logQuietly("cannot schedule null");
        }

        try {
            if (canSchedule() && getBalancer().acquirePermit()) {
                trySchedule(childStatement);
            }
        } catch (Throwable t) {
            logQuietly(t);
        }
    }

    public void finished() {
        try {
            getSchedulingStrategy().finished();
        } catch (InterruptedException e) {
            logQuietly(e);
        } finally {
            /*for (Controller slave : slaves) {
                slave.waitQuietlyIfActive();
            }*/
            Controller controller = masterController;
            if (controller != null) {
                controller.resourceReleased();
            }
        }
    }

    /**
     * Used if has shared scheduling strategy.
     */
    private class Controller {
        private final Scheduler slave;

        Controller(Scheduler slave) {
            this.slave = slave;
        }

        /**
         * Notifies the ancestor {@link Scheduler} as soon as the follower's thread has finished in
         * {@link Scheduler#finished()}.
         */
        public void resourceReleased() {
            Scheduler.this.getBalancer().releasePermit();
        }

        /**
         * @return <tt>true</tt> if new children can be scheduled.
         */
        public boolean canSchedule() {
            return Scheduler.this.canSchedule();
        }

        public void waitQuietlyIfActive() {
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
        private volatile RejectedExecutionHandler poolHandler = null;

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
