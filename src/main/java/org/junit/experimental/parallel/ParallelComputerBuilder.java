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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executing suites, classes and methods with defined concurrency. In this example the threads which completed
 * the suites and classes can be reused in parallel methods.
 * <pre>
 * ParallelComputerBuilder parallelComputerBuilder = new ParallelComputerBuilder().useOnePool(8);
 * parallelComputerBuilder.parallel(2, Type.SUITES);
 * parallelComputerBuilder.parallel(4, Type.CLASSES);
 * parallelComputerBuilder.parallel(Type.METHODS);
 * ParallelComputerBuilder.ParallelComputer computer = parallelComputerBuilder.build();
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
    public static enum Type {
        SUITES, CLASSES, METHODS;

        public static final int size = Type.values().length;
    }

    static final int TOTAL_POOL_SIZE_UNDEFINED = 0;
    private final Map<Type, Integer> parallelGroups = new LinkedHashMap<Type, Integer>(Type.size);
    private boolean useSeparatePools;
    private int totalPoolSize;

    /**
     * Calling {@link #useSeparatePools()}.
     */
    public ParallelComputerBuilder() {
        useSeparatePools();
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
     * @throws IllegalArgumentException If <tt>totalPoolSize</tt> is &lt; 3.
     */
    public ParallelComputerBuilder useOnePool(int totalPoolSize) {
        if (totalPoolSize < Type.size) {
            String msg = String.format("Size '%d' of common pool is less than %d threads.", totalPoolSize, Type.size);
            throw new IllegalArgumentException(msg);
        }
        this.totalPoolSize = totalPoolSize;
        useSeparatePools = false;
        return this;
    }

    /**
     * @return <tt>true</tt> if a type was not already set parallel
     */
    public boolean parallel(int nThreads, Type parallelType) {
        if (nThreads < 1) {
            throw new IllegalArgumentException("parallel nThreads=" + nThreads);
        }

        if (parallelType == null) {
            throw new NullPointerException("null parallelType");
        }

        if (parallelGroups.containsKey(parallelType)) {
            return false;
        } else {
            parallelGroups.put(parallelType, nThreads);
            return true;
        }
    }

    public boolean parallel(Type parallelType) {
        return parallel(Integer.MAX_VALUE, parallelType);
    }

    public ParallelComputer build() {
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
            allGroups = new LinkedHashMap<Type, Integer>(ParallelComputerBuilder.this.parallelGroups);
            Collection<Type> remaining = new ArrayList<Type>();
            Collections.addAll(remaining, Type.values());
            remaining.removeAll(allGroups.keySet());
            for (Type singleThreadType : remaining) {
                allGroups.put(singleThreadType, 1);
            }
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
            Suite suiteSuites = new RunnersSuite(suites);
            Suite suiteClasses = new RunnersSuite(classes);
            Suite all = new RunnersSuite(suiteSuites, suiteClasses);
            int poolSize = totalPoolSize();
            ExecutorService commonPool = splitPool ? null : createPool(poolSize);
            master = createMaster(commonPool, poolSize);
            setSchedulers(commonPool, suiteSuites, suiteClasses);
            all.setScheduler(master);
            return all;
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
            if (!areSuitesAndClassesParallel()) {
                return new Scheduler(null, SchedulingStrategies.createInvokerStrategy());
            } else if (pool != null && poolSize == Integer.MAX_VALUE) {
                return new Scheduler(null, SchedulingStrategies.createParallelSharedStrategy(pool));
            } else {
                return new Scheduler(null, SchedulingStrategies.createParallelStrategy(2));
            }
        }

        private boolean areSuitesAndClassesParallel() {
            return !suites.isEmpty() && !classes.isEmpty();
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
                boolean isMoreThanOneThread = false;
                for (int nThreads : allGroups.values()) {
                    isMoreThanOneThread |= nThreads > 1;
                    total += nThreads;
                    if (total < 0) {
                        total = Integer.MAX_VALUE;
                        break;
                    }
                }
                return isMoreThanOneThread ? total : 1;
            } else {
                return poolCapacity;
            }
        }

        private void setSchedulers(ExecutorService pool, Suite suiteSuites, Suite suiteClasses) {
            boolean isSharingPool = pool != null;
            int parallelSuites = allGroups.get(Type.SUITES);
            int parallelClasses = allGroups.get(Type.CLASSES);
            int parallelMethods = allGroups.get(Type.METHODS);

            Scheduler suitesScheduler =
                    isSharingPool ? createScheduler(master, pool, parallelSuites) : createScheduler(master, parallelSuites);
            suiteSuites.setScheduler(suitesScheduler);
            ArrayList<Suite> allSuites = new ArrayList<Suite>(suites);
            allSuites.addAll(nestedSuites);
            allSuites.add(suiteClasses);
            setChildScheduler(allSuites, parallelClasses, isSharingPool, pool);

            ArrayList<ParentRunner> allClasses = new ArrayList<ParentRunner>(classes);
            allClasses.addAll(nestedClasses);
            setChildScheduler(allClasses, parallelMethods, isSharingPool, pool);
        }

        private void setChildScheduler(Iterable<? extends ParentRunner> children, int permits, boolean isSharingPool, ExecutorService pool) {
            if (isSharingPool) {
                Balancer concurrency = permits == Integer.MAX_VALUE ? new Balancer() : new Balancer(permits, true);
                for (ParentRunner child : children) {
                    child.setScheduler(createScheduler(child.getDescription(), master, pool, concurrency));
                }
            } else {
                pool = Executors.newFixedThreadPool(permits);
                for (ParentRunner child : children) {
                    SchedulingStrategy strategy = SchedulingStrategies.createParallelSharedStrategy(pool);
                    child.setScheduler(new Scheduler(child.getDescription(), master, strategy));
                }
            }
        }

        private Scheduler createScheduler(Description desc, Scheduler parent, ExecutorService pool, Balancer concurrency) {
            final SchedulingStrategy strategy = SchedulingStrategies.createParallelSharedStrategy(pool);
            return parent == null ? new Scheduler(desc, strategy, concurrency) : new Scheduler(desc, parent, strategy, concurrency);
        }

        private Scheduler createScheduler(Scheduler parent, ExecutorService pool, int concurrency) {
            final SchedulingStrategy strategy = SchedulingStrategies.createParallelSharedStrategy(pool);
            final boolean isInfinite = concurrency == Integer.MAX_VALUE;
            if (parent == null) {
                return isInfinite ? new Scheduler(null, strategy) : new Scheduler(null, strategy, concurrency);
            } else {
                return isInfinite ? new Scheduler(null, parent, strategy) : new Scheduler(null, parent, strategy, concurrency);
            }
        }

        private Scheduler createScheduler(Scheduler parent, int poolSize) {
            if (poolSize == Integer.MAX_VALUE) {
                return new Scheduler(null, parent, SchedulingStrategies.createParallelStrategyUnbounded());
            } else {
                return new Scheduler(null, parent, SchedulingStrategies.createParallelStrategy(poolSize));
            }
        }

        private boolean canUse(Runner runner) {
            return !(runner instanceof ErrorReportingRunner) && runner instanceof ParentRunner;
        }
    }
}
