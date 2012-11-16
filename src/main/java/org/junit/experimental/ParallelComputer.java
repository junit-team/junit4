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

    // fClassesFinisher is initialized with this value, see #getSuite() and #getParent()
    private int countClasses;

    @Deprecated
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

    private ParallelComputer(ThreadPoolExecutor pool) {
        this(pool, 1);
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
        boolean isUnbounded= fSinglePoolCoreSize == 0 & fSinglePoolMaxSize > 1;
        if (fHasSinglePoll && !isUnbounded && fSinglePoolCoreSize <= 1)
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
     * Parallel computer with fixed-size thred pool of concurrent classes and methods.
     * @param nThreads two threads at least
     * @return parallel computer with nThreads
     * @throws IllegalArgumentException if nThreads < 2
     */
    public static Computer classesAndMethodsBounded(int nThreads) {
        ThreadPoolExecutor pool= new ThreadPoolExecutor(nThreads, nThreads,
                                                        0L, TimeUnit.MILLISECONDS,
                                                        new LinkedBlockingQueue<Runnable>());
        return classesAndMethods(pool);
    }

    /**
     * Parallel computer for concurrent classes and methods.
     * Zero corePoolSize and maximumPoolSize greater than one is accepted.
     * @param pool unbounded or fixed-size thread pool.
     * @return computer parallelized by given pool
     * @throws IllegalArgumentException if maximumPoolSize < 2; or corePoolSize < 2 for fixed-size pool
     * ; or the pool is shut down
     * @throws NullPointerException pool is null
     */
    public static Computer classesAndMethods(ThreadPoolExecutor pool) {
        return classesAndMethods(pool, 1);
    }

    /**
     * Parallel computer for concurrent classes and methods.
     * Zero corePoolSize and maximumPoolSize greater than one is accepted.
     * @param pool unbounded or fixed-size thread pool.
     * @param minConcurrentMethods 1 to (pool capacity - 1)
     * @return computer parallelized by given pool
     * @throws IllegalArgumentException if maximumPoolSize < 2; or corePoolSize < 2 for fixed-size pool
     * ; or minConcurrentMethods < 1; or the pool is shut down
     * @throws NullPointerException pool is null
     */
    public static Computer classesAndMethods(ThreadPoolExecutor pool, int minConcurrentMethods) {
        return new ParallelComputer(pool, minConcurrentMethods);
    }

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
                if (isClassPool & fHasSinglePoll) fSinglePoolBalancer.acquireUninterruptibly();
                Future<?> f= service.submit(childStatement);
                if (!isClassPool & fProvidedPools) fTaskFutures.add(f);
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
                maxConcurrentClasses= Math.min(maxConcurrentClasses, countClasses);
                fSinglePoolBalancer= new Semaphore(maxConcurrentClasses);
            }
            fClassesFinisher= fProvidedPools ? new CountDownLatch(countClasses) : null;
            if (fParallelClasses) parallelize((ParentRunner) suite, threadPoolClasses(), true);
        }
        return suite;
    }

    @Override
    protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
            throws Throwable {
        Runner runner = super.getRunner(builder, testClass);
        if (canSchedule(runner)) {
            ++countClasses;
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
            } catch (ExecutionException e) {/*fired in run-notifier*/}
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
