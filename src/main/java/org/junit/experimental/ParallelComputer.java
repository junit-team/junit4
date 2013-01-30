package org.junit.experimental;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;
import org.junit.internal.runners.ErrorReportingRunner;

/**
 * Represents a factory of Computers scheduling concurrent JUnit classes and methods in several variants.
 * <p><pre>
 * public static class Slow2 extends Slow1 {}
 * 
 * public static class Slow1 {
 *     &#064;Test
 *     public void slow() throws InterruptedException {
 *         Thread.sleep(900);
 *     }
 * 
 *     &#064;Test
 *     public void slower() throws InterruptedException {
 *         Thread.sleep(1100);
 *     }
 * }
 * </pre>
 * <p>
 * <h3>Executing parallel classes</h3>
 * This executes parallel classes in one infinite-size thread pool.
 * <p><pre>
 * JUnitCore.runClasses(ParallelComputer.classes(), Slow1.class, Slow2.class);
 * </pre>
 * 
 * <h3>Executing parallel methods</h3>
 * This creates a new infinite-size thread pool per class and executes its methods in parallel.
 * <p><pre>
 * JUnitCore.runClasses(ParallelComputer.methods(), Slow1.class, Slow2.class);
 * </pre>
 * 
 * <h3>Executing parallel classes and methods</h3>
 * <p><pre>
 * Executes parallel classes and methods in one infinite-size thread pool.
 * JUnitCore.runClasses(ParallelComputer.classesAndMethodsUnbounded(), Slow1.class, Slow2.class);
 * </pre>
 * 
 * @author tibor17
 * @since 4.12
 * @see <a href="https://github.com/KentBeck/junit/wiki/ParallelComputer">ParallelComputer at JUnit wiki</a>
 */
public class ParallelComputer extends Computer {

    /**
     * {@code true} if has to run classes in parallel.
     */
    private final boolean fParallelClasses;

    /**
     * {@code true} if has to run methods in parallel.
     */
    private final boolean fParallelMethods;

    /**
     * {@code true} if called a factory method which specifies a thread pool.
     */
    private final boolean fProvidedPools;

    /**
     * {@code true} if {@link #classesAndMethods(ThreadPoolExecutor)} or {@link #classesAndMethods(ThreadPoolExecutor, int)}.
     */
    private final boolean fHasSinglePoll;

    /**
     * Is {@link ThreadPoolExecutor#getCorePoolSize()} if {@link #fHasSinglePoll} is set.
     * To compute num of parallel methods {@link #fSinglePoolMinConcurrentMethods} in single pool #classesAndMethods().
     */
    private final int fSinglePoolCoreSize;

    /**
     * {@link ThreadPoolExecutor#getMaximumPoolSize()} if {@link #fHasSinglePoll} is set. To compute num of parallel methods.
     */
    private final int fSinglePoolMaxSize;

    private final int fSinglePoolMinConcurrentMethods;

    /**
     * Thread pools if specified in factories.
     */
    private final ExecutorService fPoolClasses;
    private final ExecutorService fPoolMethods;

    /**
     * To return executed parents and children before {@link #shutdown(boolean)}.
     */
    private final ConcurrentLinkedQueue<Description> fBeforeShutdown= new ConcurrentLinkedQueue<Description>();

    /**
     * {@code true} if shutdown.
     */
    private final AtomicBoolean fIsShutDown= new AtomicBoolean(false);

    /**
     * If {@link #fHasSinglePoll}, waiting in {@link RunnerScheduler#finished()} until all classes have completed.
     */
    private volatile CountDownLatch fClassesFinisher;

    /**
     * If {@link #fHasSinglePoll}, ensures min num of parallel methods is {@link #fSinglePoolMinConcurrentMethods}.
     * Prevents from all thread resources exhaustion in favor of parallel classes.
     */
    private volatile Semaphore fSinglePoolBalancer;

    private volatile int fCountClasses;

    /**
     * @deprecated As of JUnit 4.12, replace by {@link #methods()}, {@link #classes()}
     *             and {@link #classesAndMethodsUnbounded()}.
     */
    @Deprecated
    public ParallelComputer(boolean classes, boolean methods) {
        // to satisfy JVM spec -write operation first on volatile fields
        fClassesFinisher= null;
        fSinglePoolBalancer= null;
        fCountClasses= 0;
        fParallelClasses= classes;
        fParallelMethods= methods;
        fPoolClasses= null;
        fPoolMethods= null;
        fProvidedPools= false;
        fHasSinglePoll= fProvidedPools;
        fSinglePoolCoreSize= -1;
        fSinglePoolMaxSize= -1;
        fSinglePoolMinConcurrentMethods= -1;
    }

    private ParallelComputer(ExecutorService poolClasses, ExecutorService poolMethods) {
        this(poolClasses, poolMethods, -1, -1, -1);
    }

    private ParallelComputer(ThreadPoolExecutor pool, int minConcurrentMethods) {
        this(pool, pool, pool.getCorePoolSize(), pool.getMaximumPoolSize(), minConcurrentMethods);
    }

    private ParallelComputer(ExecutorService poolClasses, ExecutorService poolMethods,
                            int singlePoolCoreSize, int singlePoolMaxSize,
                            int minConcurrentMethods) {
        // to satisfy JVM spec -write operation first on volatile fields
        fClassesFinisher= null;
        fSinglePoolBalancer= null;
        fCountClasses= 0;

        if (poolClasses == null && poolMethods == null) {
            throw new NullPointerException("null classes/methods executor");
        }

        if (poolClasses != null && poolClasses.isShutdown()) {
            throw new IllegalStateException(poolClasses +
                    " provided classes executor is in shutdown state and cannot restart");
        }

        if (poolMethods != null && poolMethods.isShutdown()) {
            throw new IllegalStateException(poolMethods +
                    " provided methods executor is in shutdown state and cannot restart");
        }

        fParallelClasses= poolClasses != null;
        fParallelMethods= poolMethods != null;
        fPoolClasses= poolClasses;
        fPoolMethods= poolMethods;
        fProvidedPools= true;
        fHasSinglePoll= fPoolClasses == fPoolMethods;
        fSinglePoolCoreSize= fHasSinglePoll ? singlePoolCoreSize : -1;
        fSinglePoolMaxSize= fHasSinglePoll ? singlePoolMaxSize : -1;
        fSinglePoolMinConcurrentMethods= fHasSinglePoll ? minConcurrentMethods : -1;

        final boolean isFixedSize= fSinglePoolCoreSize == fSinglePoolMaxSize;
        if (fHasSinglePoll && isFixedSize && fSinglePoolCoreSize <= 1) {
            throw new IllegalArgumentException("core pool size " + fSinglePoolCoreSize + " should be > 1");
        }

        if (fHasSinglePoll && fSinglePoolMaxSize <= 1) {
            throw new IllegalArgumentException("max pool size " + fSinglePoolMaxSize + " should be > 1");
        }

        if (fHasSinglePoll && fSinglePoolMinConcurrentMethods < 1) {
            throw new IllegalArgumentException("min concurrent methods " + fSinglePoolMinConcurrentMethods + " should be >= 1");
        }

        if (fHasSinglePoll && fSinglePoolMinConcurrentMethods >= fSinglePoolMaxSize) {
            throw new IllegalArgumentException("min methods pool size should be less than max pool size");
        }

        addDefaultShutdownHandler();
    }

    public static Computer classes() {
        return new ParallelComputer(true, false);
    }

    public static Computer methods() {
        return new ParallelComputer(false, true);
    }

    public static ParallelComputer methods(ExecutorService pool) {
        if (pool == null) {
            throw new NullPointerException("null methods executor");
        }

        return new ParallelComputer(null, pool);
    }

    public static ParallelComputer classes(ExecutorService pool) {
        if (pool == null) {
            throw new NullPointerException("null classes executor");
        }

        return new ParallelComputer(pool, null);
    }

	/**
     * Parallel computer with infinitive thread pool size.
     * @return parallel computer
     */
    public static ParallelComputer classesAndMethodsUnbounded() {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                                        60L, TimeUnit.SECONDS,
                                                        new SynchronousQueue<Runnable>());
        return classesAndMethods(pool);
    }

    /**
     * Parallel computer with fixed-size thread pool of concurrent classes and methods.
     * @param nThreads two threads at least
     * @return parallel computer with <tt>nThreads</tt>
     * @throws IllegalArgumentException if <tt>nThreads &lt; 2</tt>
     */
    public static ParallelComputer classesAndMethodsBounded(int nThreads) {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(nThreads, nThreads,
                                                        0L, TimeUnit.MILLISECONDS,
                                                        new LinkedBlockingQueue<Runnable>());
        return classesAndMethods(pool);
    }

    /**
     * Parallel computer for concurrent classes and methods.
     * Zero <tt>corePoolSize</tt> and <tt>maximumPoolSize</tt> greater than one is accepted.
     * @param pool unbounded or fixed-size thread pool.
     * @return computer parallelized by given pool
     * @throws IllegalArgumentException if <tt>maximumPoolSize &lt; 2</tt>;
     *         or <tt>corePoolSize &lt; 2</tt> for fixed-size pool
     * @throws IllegalStateException if the pool is already shutdown
     * @throws NullPointerException <tt>pool</tt> is null
     */
    public static ParallelComputer classesAndMethods(ThreadPoolExecutor pool) {
        return classesAndMethods(pool, 1);
    }

    /**
     * Parallel computer for concurrent classes and methods.
     * Zero <tt>corePoolSize</tt> and <tt>maximumPoolSize</tt> greater than one is accepted.
     * @param pool unbounded or fixed-size thread pool.
     * @param minConcurrentMethods 1 to (pool capacity - 1)
     * @return computer parallelized by given pool
     * @throws IllegalArgumentException if <tt>maximumPoolSize &lt; 2</tt>;
     *         or <tt>corePoolSize &lt; 2</tt> for fixed-size pool;
     *         or <tt>minConcurrentMethods &lt; 1<tt>
     * @throws IllegalStateException if the pool is already shutdown
     * @throws NullPointerException <tt>pool</tt> is null
     */
    public static ParallelComputer classesAndMethods(ThreadPoolExecutor pool, int minConcurrentMethods) {
        return new ParallelComputer(pool, minConcurrentMethods);
    }

    /**
     * Parallel computer for concurrent classes and methods.
     * Zero <tt>corePoolSize</tt> and <tt>maximumPoolSize</tt> greater than one is accepted.
     * @throws NullPointerException if a pool is null
     */
    public static ParallelComputer classesAndMethods(ExecutorService poolClasses, ExecutorService poolMethods) {
        if (poolClasses == null) {
            throw new NullPointerException("null classes executor");
        }

        if (poolMethods == null) {
            throw new NullPointerException("null methods executor");
        }

        if (poolClasses == poolMethods) {
            throw new IllegalArgumentException("instead call #classesAndMethods(ThreadPoolExecutor, int)");
        }

        return new ParallelComputer(poolClasses, poolMethods);
    }

    /**
     * @return <tt>true</tt> if a parallel scheduler is set in the <tt>runner</tt>
     */
    private boolean tryParallelize(final ParentRunner runner, final ExecutorService service, final boolean isClasses) {
        runner.setScheduler(new RunnerScheduler() {
            private final ConcurrentLinkedQueue<Future<?>> fMethodsFutures
                = !isClasses & fProvidedPools & fParallelMethods ? new ConcurrentLinkedQueue<Future<?>>() : null;

            private void run(Runnable child) {
                fBeforeShutdown.add(runner.getDescription());
                child.run();
            }

            private void submit(Runnable child) {
                try {
                    Future<?> f= service.submit(child);
                    if (!isClasses & fProvidedPools & fParallelMethods) {
                        fMethodsFutures.add(f);
                    }
                    fBeforeShutdown.add(runner.getDescription());
                } catch (RejectedExecutionException e) {
                    // after external shutdown
                    shutdownQuietly(false);
                }
            }

            public void schedule(Runnable childStatement) {
                if (shouldEscapeSchedulingTasks()) {
                    return;
                }

                if (isClasses & fHasSinglePoll) {
                    fSinglePoolBalancer.acquireUninterruptibly();
                }

                if (shouldEscapeSchedulingTasks()) {
                    return;
                }

                if (service == null) {
                    run(childStatement);
                } else {
                    submit(childStatement);
                }
            }

            public void finished() {
                if (isClasses) {
                    // wait until all test cases finished
                    awaitClassesFinished();
                    tryFinish(service);
                } else {
                    // wait until the test case finished
                    if (fProvidedPools) {
                        awaitClassFinished(fMethodsFutures);
                        if (fHasSinglePoll) {
                            fSinglePoolBalancer.release();
                        }
                    } else {
                        tryFinish(service);
                    }
                }
            }
        });

        return service != null;
    }

    private static void tryFinish(ExecutorService pool) {
        if (pool == null) return;
        try {
            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public Runner getSuite(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
        Runner suite= super.getSuite(builder, classes);
        if (canSchedule(suite)) {
            if (fHasSinglePoll) {
                int maxConcurrentClasses;
                if (fSinglePoolCoreSize == 0 || fSinglePoolCoreSize == fSinglePoolMaxSize) {
                    // is unbounded or fixed-size single pool
                    maxConcurrentClasses= fSinglePoolMaxSize - fSinglePoolMinConcurrentMethods;
                } else {
                    maxConcurrentClasses= Math.max(1, fSinglePoolCoreSize - fSinglePoolMinConcurrentMethods);
                }
                maxConcurrentClasses= Math.min(maxConcurrentClasses, fCountClasses);
                fSinglePoolBalancer= new Semaphore(maxConcurrentClasses);
            }
            fClassesFinisher= fHasSinglePoll ? new CountDownLatch(fCountClasses) : null;
            tryParallelize((ParentRunner) suite, threadPoolClasses(), true);
        }
        return suite;
    }

    @Override
    protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable {
        Runner runner= super.getRunner(builder, testClass);
        if (canSchedule(runner)) {
            // incremented without been AtomicInteger because not yet concurrent access
            ++fCountClasses;
            tryParallelize((ParentRunner) runner, threadPoolMethods(), false);
        }
        return runner;
    }

    /**
     * Creates a New Cached Thread Pool to schedule methods in a test class.
     * This method is called if ParallelComputer is instantiate by {@link #ParallelComputer(boolean, boolean)}.
     * @return new Thread pool
     */
    protected ExecutorService newThreadPoolClasses() {
        return Executors.newCachedThreadPool();
    }

    /**
     * Creates a New Cached Thread Pool to schedule a test case.
     * This method is called if ParallelComputer is instantiate by {@link #ParallelComputer(boolean, boolean)}.
     * @return new Thread pool
     */
    protected ExecutorService newThreadPoolMethods() {
        return Executors.newCachedThreadPool();
    }

    private boolean canSchedule(Runner runner) {
        return !(runner instanceof ErrorReportingRunner) &&
                runner instanceof ParentRunner &&
				(fParallelClasses || fParallelMethods);
    }

    private ExecutorService threadPoolClasses() {
        return !fProvidedPools && fParallelClasses ? newThreadPoolClasses() : fPoolClasses;
    }

    private ExecutorService threadPoolMethods() {
        return !fProvidedPools && fParallelMethods ? newThreadPoolMethods() : fPoolMethods;
    }

    private void awaitClassFinished(Queue<Future<?>> methodsFutures) {
        if (methodsFutures != null) {
            // fParallelMethods should be false
            for (Future<?> methodFuture : methodsFutures) {
                try {
                    methodFuture.get();
                } catch (InterruptedException e) {
                    // after called external ExecutorService#shutdownNow()
                    shutdownQuietly(true);
                } catch (ExecutionException e) {
                    // test throws exception
                } catch (CancellationException e) {
                    // cannot happen because not calling Future#cancel()
                }
            }
        }

        if (fHasSinglePoll) {
            fClassesFinisher.countDown();
        }
    }

    private void awaitClassesFinished() {
        if (fHasSinglePoll) {
            try {
                fClassesFinisher.await();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private void addDefaultShutdownHandler() {
        if (fProvidedPools) {
            if (fParallelClasses) {
                setDefaultShutdownHandler(fPoolClasses);
            }

            if (!fHasSinglePoll && fParallelMethods) {
                setDefaultShutdownHandler(fPoolMethods);
            }
        }
    }

    private void setDefaultShutdownHandler(Executor executor) {
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor pool= (ThreadPoolExecutor) executor;
            RejectedExecutionHandler poolHandler= pool.getRejectedExecutionHandler();
            pool.setRejectedExecutionHandler(new ShutdownHandler(poolHandler));
        }
    }

    private boolean shouldEscapeSchedulingTasks() {
        return fIsShutDown.get();
    }

    private void stopScheulingTasks() {
        fIsShutDown.set(true);
    }

    /**
     * @param shutdownNow if <tt>false</tt> don't call pool's methods
     */
    private void shutdownQuietly(boolean shutdownNow) {
        try {
            // let other threads avoid submitting new tasks
            if (fIsShutDown.compareAndSet(false, true)) {
                if (fHasSinglePoll && fCountClasses > 0) {
                    // let dormant class-threads wake up and escape from balancer
                    fSinglePoolBalancer.release(fCountClasses);
                    // cached a value in volatile field to the stack as a constant for faster reads
                    final CountDownLatch classesFinisher= fClassesFinisher;
                    // signals that the total num of classes could not be reached
                    while (classesFinisher.getCount() > 0) {
                        classesFinisher.countDown();
                    }
                }
            }

            if (fProvidedPools) {
                if (fParallelClasses) {
                    shutdown(fPoolClasses, shutdownNow);
                }

                if (!fHasSinglePoll && fParallelMethods) {
                    shutdown(fPoolMethods, shutdownNow);
                }
            }
        } catch (Throwable t) {
            // may be only OOM, security and permission exceptions
            t.printStackTrace(System.err);
        }
    }

    private static void shutdown(ExecutorService pool, boolean shutdownNow) {
        if (shutdownNow) {
            pool.shutdownNow();
        } else {
            pool.shutdown();
        }
    }

    /**
     * Attempts to stop all actively executing tasks and immediately returns a collection
     * of descriptions of those tasks which have completed prior to this call.
     * <p>
     * If the instance is created by {@link #classes()} or {@link #methods()}, the internal
     * thread pools escape scheduling new tasks without shutting down the {@link ExecutorService}.
     * Otherwise, the provided pools are shutdown.
     * <p>
     * If <tt>shutdownNow</tt> is set, waiting methods will cancel via {@link Thread#interrupt}.
     *
     * @param shutdownNow if <tt>true</tt> interrupts waiting methods
     * @return collection of recent descriptions
     */
    public final Collection<Description> shutdown(boolean shutdownNow) {
        if (fProvidedPools) {
            shutdownQuietly(shutdownNow);
        } else {
            stopScheulingTasks();
        }

        return new ArrayList<Description>(fBeforeShutdown);
    }

    private final class ShutdownHandler implements RejectedExecutionHandler {
        private final RejectedExecutionHandler fPoolHandler;

        ShutdownHandler(RejectedExecutionHandler poolHandler) {
            fPoolHandler= poolHandler;
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                shutdownQuietly(false);
            }
            fPoolHandler.rejectedExecution(r, executor);
        }
    }
}
