package org.junit.experimental.parallel;

import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executing suites, classes and methods with defined concurrency. In this example the threads which completed
 * the suites and classes can be reused in parallel methods.
 * <pre>
 * ParallelComputerBuilder builder = new ParallelComputerBuilder();
 * builder.useOnePool(8).parallelSuites(2).parallelClasses(4).parallelMethods();
 * ParallelComputerBuilder.ParallelComputer computer = builder.buildComputer();
 * Class<?>[] tests = {...};
 * new JUnitCore().run(computer, tests);
 * </pre>
 * Note that the type has always at least one thread even if unspecified. The capacity in
 * {@link ParallelComputerBuilder#useOnePool(int)} must be greater than the number of concurrent suites and classes altogether.
 * <p>
 * The Computer can be shutdown in a separate thread. Pending tests will be interrupted if the argument is <tt>true</tt>.
 * <pre>
 * computer.shutdown(true);
 * </pre>
 *
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class ParallelComputerBuilder {
    private static enum Type {
        SUITES, CLASSES, METHODS
    }

    static final int TOTAL_POOL_SIZE_UNDEFINED = 0;
    private final Map<Type, Integer> parallelGroups = new HashMap<Type, Integer>(3);
    private boolean useSeparatePools;
    private int totalPoolSize;

    /**
     * Calling {@link #useSeparatePools()}.
     */
    public ParallelComputerBuilder() {
        useSeparatePools();
        parallelGroups.put(Type.SUITES, 0);
        parallelGroups.put(Type.CLASSES, 0);
        parallelGroups.put(Type.METHODS, 0);
    }

    public ParallelComputerBuilder useSeparatePools() {
        totalPoolSize = TOTAL_POOL_SIZE_UNDEFINED;
        useSeparatePools = true;
        return this;
    }

    public ParallelComputerBuilder useOnePool() {
        totalPoolSize = TOTAL_POOL_SIZE_UNDEFINED;
        useSeparatePools = false;
        return this;
    }

    /**
     * @param totalPoolSize Pool size where suites, classes and methods are executed in parallel.
     * @throws IllegalArgumentException If <tt>totalPoolSize</tt> is &lt; 1.
     */
    public ParallelComputerBuilder useOnePool(int totalPoolSize) {
        if (totalPoolSize < 1) {
            throw new IllegalArgumentException("Size of common pool is less than 1.");
        }
        this.totalPoolSize = totalPoolSize;
        useSeparatePools = false;
        return this;
    }

    public ParallelComputerBuilder parallelSuites() {
        return parallel(Type.SUITES);
    }

    public ParallelComputerBuilder parallelSuites(int nThreads) {
        return parallel(nThreads, Type.SUITES);
    }

    public ParallelComputerBuilder parallelClasses() {
        return parallel(Type.CLASSES);
    }

    public ParallelComputerBuilder parallelClasses(int nThreads) {
        return parallel(nThreads, Type.CLASSES);
    }

    public ParallelComputerBuilder parallelMethods() {
        return parallel(Type.METHODS);
    }

    public ParallelComputerBuilder parallelMethods(int nThreads) {
        return parallel(nThreads, Type.METHODS);
    }

    private ParallelComputerBuilder parallel(int nThreads, Type parallelType) {
        if (nThreads < 0) {
            throw new IllegalArgumentException("negative nThreads " + nThreads);
        }

        if (parallelType == null) {
            throw new NullPointerException("null parallelType");
        }

        parallelGroups.put(parallelType, nThreads);
        return this;
    }

    private ParallelComputerBuilder parallel(Type parallelType) {
        return parallel(Integer.MAX_VALUE, parallelType);
    }

    public ParallelComputer buildComputer() {
        return new ParallelComputer();
    }

    private static class RunnersSuite extends Suite {
        protected RunnersSuite(Collection<? extends ParentRunner> runners) throws InitializationError {
            super(null, new ArrayList<Runner>(runners));
        }

        protected RunnersSuite(Runner... runners) throws InitializationError {
            super(null, Arrays.asList(runners));
        }
    }

    public final class ParallelComputer extends Computer {
        final Collection<Suite> suites = new ArrayList<Suite>();
        final Collection<Suite> nestedSuites = new ArrayList<Suite>();
        final Collection<ParentRunner> classes = new ArrayList<ParentRunner>();
        final Collection<ParentRunner> nestedClasses = new ArrayList<ParentRunner>();
        final int poolCapacity;
        final boolean splitPool;
        private final Map<Type, Integer> allGroups;
        private volatile Scheduler master;

        private ParallelComputer() {
            allGroups = new HashMap<Type, Integer>(ParallelComputerBuilder.this.parallelGroups);
            poolCapacity = ParallelComputerBuilder.this.totalPoolSize;
            splitPool = ParallelComputerBuilder.this.useSeparatePools;
        }

        public Collection<Description> shutdown(boolean shutdownNow) {
            final Scheduler master = this.master;
            return master == null ? Collections.<Description>emptyList() : master.shutdown(shutdownNow);
        }

        @Override
        public Runner getSuite(RunnerBuilder builder, Class<?>[] cls) throws InitializationError {
            super.getSuite(builder, cls);
            populateChildrenFromSuites();
            return setSchedulers();
        }

        @Override
        protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable {
            Runner runner = super.getRunner(builder, testClass);
            if (canUse(runner)) {
                if (runner instanceof Suite) {
                    suites.add((Suite) runner);
                } else {
                    classes.add((ParentRunner) runner);
                }
            }
            return runner;
        }

        private class SuiteFilter extends Filter {
            @Override
            public boolean shouldRun(Description description) {
                return true;
            }

            @Override
            public void apply(Object child) throws NoTestsRemainException {
                super.apply(child);
                if (child instanceof Suite) {
                    nestedSuites.add((Suite) child);
                } else if (child instanceof ParentRunner) {
                    nestedClasses.add((ParentRunner) child);
                }
            }

            @Override
            public String describe() {
                return "";
            }
        }

        private ExecutorService createPool(int poolSize) {
            return poolSize < Integer.MAX_VALUE ? Executors.newFixedThreadPool(poolSize) : Executors.newCachedThreadPool();
        }

        private Scheduler createMaster(ExecutorService pool, int poolSize) {
            if (!areSuitesAndClassesParallel() || poolSize <= 1) {
                return new Scheduler(null, new InvokerStrategy());
            } else if (pool != null && poolSize == Integer.MAX_VALUE) {
                return new Scheduler(null, new SharedThreadPoolStrategy(pool));
            } else {
                return new Scheduler(null, SchedulingStrategies.createParallelStrategy(2));
            }
        }

        private boolean areSuitesAndClassesParallel() {
            return !suites.isEmpty() && allGroups.get(Type.SUITES) > 0 && !classes.isEmpty() && allGroups.get(Type.CLASSES) > 0;
        }

        private void populateChildrenFromSuites() {
            Filter filter = new SuiteFilter();
            for (Iterator<Suite> it = suites.iterator(); it.hasNext();) {
                Suite suite = it.next();
                try {
                    suite.filter(filter);
                } catch (NoTestsRemainException e) {
                    it.remove();
                }
            }
        }

        private int totalPoolSize() {
            if (poolCapacity == TOTAL_POOL_SIZE_UNDEFINED) {
                int total = 0;
                for (int nThreads : allGroups.values()) {
                    total += nThreads;
                    if (total < 0) {
                        total = Integer.MAX_VALUE;
                        break;
                    }
                }
                return total;
            } else {
                return poolCapacity;
            }
        }

        private Runner setSchedulers() throws InitializationError {
            int parallelSuites = allGroups.get(Type.SUITES);
            int parallelClasses = allGroups.get(Type.CLASSES);
            int parallelMethods = allGroups.get(Type.METHODS);
            int poolSize = totalPoolSize();
            ExecutorService commonPool = splitPool || poolSize == 0 ? null : createPool(poolSize);
            master = createMaster(commonPool, poolSize);

            // a scheduler for parallel suites
            final Scheduler suitesScheduler;
            if (commonPool != null && parallelSuites > 0) {
                suitesScheduler = createScheduler(null, commonPool, true, new Balancer(parallelSuites, true));
            } else {
                suitesScheduler = createScheduler(parallelSuites);
            }
            Suite suiteSuites = new RunnersSuite(suites);
            suiteSuites.setScheduler(suitesScheduler);

            // schedulers for parallel classes
            Suite suiteClasses = new RunnersSuite(classes);
            ArrayList<Suite> allSuites = new ArrayList<Suite>(suites);
            allSuites.addAll(nestedSuites);
            allSuites.add(suiteClasses);
            setSchedulers(allSuites, parallelClasses, commonPool);

            // schedulers for parallel methods
            ArrayList<ParentRunner> allClasses = new ArrayList<ParentRunner>(classes);
            allClasses.addAll(nestedClasses);
            setSchedulers(allClasses, parallelMethods, commonPool);

            // resulting runner for Computer#getSuite() scheduled by master scheduler
            Suite all = new RunnersSuite(suiteSuites, suiteClasses);
            all.setScheduler(master);
            return all;
        }

        private void setSchedulers(Iterable<? extends ParentRunner> runners, int poolSize, ExecutorService commonPool) {
            if (commonPool != null) {
                Balancer concurrencyLimit = new Balancer(poolSize, true);
                boolean doParallel = poolSize > 0;
                for (ParentRunner runner : runners) {
                    runner.setScheduler(createScheduler(runner.getDescription(), commonPool, doParallel, concurrencyLimit));
                }
            } else {
                ExecutorService pool = null;
                if (poolSize == Integer.MAX_VALUE) {
                    pool = Executors.newCachedThreadPool();
                } else if (poolSize > 0) {
                    pool = Executors.newFixedThreadPool(poolSize);
                }
                boolean doParallel = pool != null;
                for (ParentRunner runner : runners) {
                    runner.setScheduler(createScheduler(runner.getDescription(), pool, doParallel, new Balancer()));
                }
            }
        }

        private Scheduler createScheduler(Description desc, ExecutorService pool, boolean doParallel, Balancer concurrency) {
            doParallel &= pool != null;
            SchedulingStrategy strategy = doParallel ? new SharedThreadPoolStrategy(pool) : new InvokerStrategy();
            return new Scheduler(desc, master, strategy, concurrency);
        }

        private Scheduler createScheduler(int poolSize) {
            if (poolSize == Integer.MAX_VALUE) {
                return new Scheduler(null, master, SchedulingStrategies.createParallelStrategyUnbounded());
            } else if (poolSize == 0) {
                return new Scheduler(null, master, new InvokerStrategy());
            } else {
                return new Scheduler(null, master, SchedulingStrategies.createParallelStrategy(poolSize));
            }
        }

        private boolean canUse(Runner runner) {
            return !(runner instanceof ErrorReportingRunner) && runner instanceof ParentRunner;
        }
    }
}
