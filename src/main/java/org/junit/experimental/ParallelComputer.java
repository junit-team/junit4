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
import java.util.Queue;

import org.junit.runner.Computer;
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
    // true: parallelized classes if #classes(), #classesAndMethods() or ParallelComputer(true, x), ParallelComputer(pool[,...])
    private final boolean fParallelClasses;

    // true: parallelized methods if #methods(), #classesAndMethods() or ParallelComputer(x, true), ParallelComputer(x, pool), ParallelComputer(pool[,...])
    private final boolean fParallelMethods;

    // true if a pool is provided by caller: #classes(pool), #methods(pool), #classesAndMethods(pool[,...]), ParallelComputer(pool[,...])
    // else a pool is created (same as previous implementation), see #threadPoolClasses() and #threadPoolMethods()
    private final boolean fProvidedPools;

    // true if provided pool is one common for methods/classes; enables the fSinglePoolBalancer
    private final boolean fHasSinglePoll;

    // if single pool, ThreadPoolExecutor#getCorePoolSize(); necessary to compute thread resources for classes/methods, see #getSuite()
    private final int fSinglePoolCoreSize;

    // if single pool, ThreadPoolExecutor#getMaximumPoolSize(); necessary to compute thread resources for classes/methods, see #getSuite()
    private final int fSinglePoolMaxSize;

    // if single pool, the user can specify min concurrent methods. Without this the parallel classes consumed all thread resources.
    private final int fSinglePoolMinConcurrentMethods;

    // provided pools
    // if a single pool, both refer to the same
    private final ExecutorService fPoolClasses;
    private final ExecutorService fPoolMethods;

    // disables the callers Thread for scheduling purposes until all classes finished
    private volatile CountDownLatch fClassesFinisher;

    // prevents thread resources exhaustion on classes when used with single pool => gives a chance to parallelize methods
    // see fSinglePoolMinConcurrentMethods above
    // used in #parallelize(): allows a number of parallel classes and methods
    private volatile Semaphore fSinglePoolBalancer;

    //set if a pool is shut down externally
    private final AtomicBoolean fIsShutDown= new AtomicBoolean();

    // fClassesFinisher is initialized with this value, see #getSuite() and #getParent()
    private volatile int fCountClasses;

    @Deprecated//should be private
    public ParallelComputer(boolean classes, boolean methods) {
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
        if (poolClasses == null && poolMethods == null)
            throw new NullPointerException("null classes/methods executor");
        if (poolClasses != null && poolClasses.isShutdown())
            throw new IllegalStateException(poolClasses
                + " provided classes executor is in shutdown state and cannot restart");
        if (poolMethods != null && poolMethods.isShutdown())
            throw new IllegalStateException(poolMethods
                + " provided methods executor is in shutdown state and cannot restart");
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
        if (fHasSinglePoll && isFixedSize && fSinglePoolCoreSize <= 1)
            throw new IllegalArgumentException("core pool size " + fSinglePoolCoreSize + " should be > 1");
        if (fHasSinglePoll && fSinglePoolMaxSize <= 1)
            throw new IllegalArgumentException("max pool size " + fSinglePoolMaxSize + " should be > 1");
        if (fHasSinglePoll && fSinglePoolMinConcurrentMethods < 1)
            throw new IllegalArgumentException("min concurrent methods " + fSinglePoolMinConcurrentMethods + " should be >= 1");
        if (fHasSinglePoll && fSinglePoolMinConcurrentMethods >= fSinglePoolMaxSize)
            throw new IllegalArgumentException("min methods pool size should be less than max pool size");
    }

    public static Computer classes() {
        return new ParallelComputer(true, false);
    }

    public static Computer methods() {
        return new ParallelComputer(false, true);
    }

    public static Computer methods(ExecutorService pool) {
        if (pool == null) throw new NullPointerException("null methods executor");
        return new ParallelComputer(null, pool);
    }

    public static Computer classes(ExecutorService pool) {
        if (pool == null) throw new NullPointerException("null classes executor");
        return new ParallelComputer(pool, null);
    }

	/**
     * Parallel computer with infinitive thread pool size.
     * @return parallel computer
     */
    public static Computer classesAndMethodsUnbounded() {
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
    public static Computer classesAndMethodsBounded(int nThreads) {
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
     * @throws IllegalStateException if the pool is already shut down
     * @throws NullPointerException <tt>pool</tt> is null
     */
    public static Computer classesAndMethods(ThreadPoolExecutor pool) {
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
     * @throws IllegalStateException if the pool is already shut down
     * @throws NullPointerException <tt>pool</tt> is null
     */
    public static Computer classesAndMethods(ThreadPoolExecutor pool, int minConcurrentMethods) {
        return new ParallelComputer(pool, minConcurrentMethods);
    }

    /**
     * Parallel computer for concurrent classes and methods.
     * Zero <tt>corePoolSize</tt> and <tt>maximumPoolSize</tt> greater than one is accepted.
     * @throws NullPointerException if a pool is null
     */
    public static Computer classesAndMethods(ExecutorService poolClasses, ExecutorService poolMethods) {
        if (poolClasses == null) throw new NullPointerException("null classes executor");
        if (poolMethods == null) throw new NullPointerException("null methods executor");
		if (poolClasses == poolMethods)
            throw new IllegalArgumentException("instead call #classesAndMethods(ThreadPoolExecutor, int)");
        return new ParallelComputer(poolClasses, poolMethods);
    }

    private Runner sequencer(ParentRunner runner) {
        runner.setScheduler(new RunnerScheduler() {
            public void schedule(Runnable childStatement) {
                childStatement.run();
            }

            public void finished() {
                if (fClassesFinisher != null) fClassesFinisher.countDown();
            }
        });
        return runner;
    }

    private Runner parallelize(final ParentRunner runner, final ExecutorService service, final boolean isClassPool) {
        runner.setScheduler(new RunnerScheduler() {
            private final ConcurrentLinkedQueue<Future<?>> fTaskFutures
                = !isClassPool & fProvidedPools ? new ConcurrentLinkedQueue<Future<?>>() : null;

            public void schedule(Runnable childStatement) {
                if (fIsShutDown.get()) return;
                if (isClassPool & fHasSinglePoll) fSinglePoolBalancer.acquireUninterruptibly();
                if (fIsShutDown.get()) return;
                try {
                    Future<?> f= service.submit(childStatement);
                    if (!isClassPool & fProvidedPools) fTaskFutures.add(f);
                } catch (RejectedExecutionException e) {
                    /*external shut down*/
                    if (fIsShutDown.compareAndSet(false, true)) {//let other threads avoid submitting new tasks
                        if (fHasSinglePoll) {
                            //let dormant class-threads wake up and escape from balancer
                            fSinglePoolBalancer.release(fCountClasses);
                        }
                        if (fProvidedPools) {
                            //signal that the total num classes will not be reached
                            while (fClassesFinisher.getCount() > 0)
                                fClassesFinisher.countDown();
                        }
                    }
                }
            }

            public void finished() {
                if (isClassPool) { //wait until all test cases finished
                    if (fProvidedPools) awaitClassesFinished();
                    tryFinish(service);
                } else { //wait until the test case finished
                    if (fProvidedPools) {
                        awaitClassFinished(fTaskFutures);
                        if (fHasSinglePoll) fSinglePoolBalancer.release();
                    } else tryFinish(service);
                }
            }
        });
        return runner;
    }

    private static void tryFinish(ExecutorService pool) {
        try {
            pool.shutdown();
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public Runner getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes)
            throws InitializationError {
        Runner suite = super.getSuite(builder, classes);
        if (canSchedule(suite)) {
            if (fHasSinglePoll) {
                int maxConcurrentClasses;
                if (fSinglePoolCoreSize == 0 || fSinglePoolCoreSize == fSinglePoolMaxSize)
                    //is unbounded or fixed-size single pool
                    maxConcurrentClasses= fSinglePoolMaxSize - fSinglePoolMinConcurrentMethods;
                else maxConcurrentClasses= Math.max(1, fSinglePoolCoreSize - fSinglePoolMinConcurrentMethods);
                maxConcurrentClasses= Math.min(maxConcurrentClasses, fCountClasses);
                fSinglePoolBalancer= new Semaphore(maxConcurrentClasses);
            }
            fClassesFinisher= fProvidedPools ? new CountDownLatch(fCountClasses) : null;
            if (fParallelClasses) parallelize((ParentRunner) suite, threadPoolClasses(), true);
        }
        return suite;
    }

    @Override
    protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
            throws Throwable {
        Runner runner = super.getRunner(builder, testClass);
        if (canSchedule(runner)) {
            ++fCountClasses;//incremented without been AtomicInteger because not yet concurrent access
            ParentRunner child= (ParentRunner) runner;
            runner= fParallelMethods ? parallelize(child, threadPoolMethods(), false) : sequencer(child);
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
        return fProvidedPools ? fPoolClasses : newThreadPoolClasses();
    }

    private ExecutorService threadPoolMethods() {
        return fProvidedPools ? fPoolMethods : newThreadPoolMethods();
    }

    private void awaitClassFinished(Queue<Future<?>> children) {
        for (Future<?> child : children) {
            try {
                child.get();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            } catch (ExecutionException e) {
                /*cause fired in run-notifier*/
            } catch (CancellationException e) {
                /*cannot happen because not calling Future#cancel(boolean)*/
            }
        }
        fClassesFinisher.countDown();
    }

    private void awaitClassesFinished() {
        try {
            fClassesFinisher.await();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }
}
