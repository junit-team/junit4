package org.junit.tests.experimental.parallel;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.parallel.Scheduler;
import org.junit.experimental.parallel.Balancer;
import org.junit.experimental.parallel.SchedulingStrategies;
import org.junit.experimental.parallel.SchedulingStrategy;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.runner.Description.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.experimental.parallel.SchedulingStrategies.*;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.runner.Description.createSuiteDescription;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class ParallelAbstractionTest {
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Before
    public void before() {
        counter.set(0);
    }

    private static Scheduler createScheduler(Description description, SchedulingStrategy strategy, int concurrency) {
        return new Scheduler(description, strategy, concurrency) {
            protected void logQuietly(Throwable t) {
                logQuietly(t.getLocalizedMessage());
            }

            protected void logQuietly(String msg) {
                fail(msg);
            }
        };
    }

    /**
     * The invoker executes runnable while scheduled.
     */
    @Test
    public void schedulerWithInvoker() {
        SchedulingStrategy strategy = SchedulingStrategies.createInvokerStrategy();
        Scheduler scheduler = createScheduler(EMPTY, strategy, -1);

        final AtomicBoolean doneNonParallel = new AtomicBoolean(false);

        scheduler.schedule(new Runnable() {
            public void run() {
                doneNonParallel.set(true);
            }
        });

        assertTrue(doneNonParallel.get());

        scheduler.finished();
    }

    /**
     * Runnable should be finished after the finished() has returned.
     */
    @Test(timeout = 500)
    public void schedulerWithParallelStrategy() {
        SchedulingStrategy strategy = SchedulingStrategies.createParallelStrategyUnbounded();
        Scheduler scheduler = createScheduler(EMPTY, strategy, -1);

        final AtomicBoolean doneParallel = new AtomicBoolean(false);
        final Semaphore barrier = new Semaphore(1);

        scheduler.schedule(new Runnable() {
            public void run() {
                try {
                    boolean schedulerNotHangs = barrier.tryAcquire(200, TimeUnit.MILLISECONDS);
                    assertTrue(schedulerNotHangs);
                } catch (InterruptedException e) {
                    fail();
                }
                doneParallel.set(true);
            }
        });

        assertFalse(doneParallel.get());

        barrier.release();

        scheduler.finished();

        assertTrue(doneParallel.get());
    }

    public static class C {
        @BeforeClass
        public static void before() {
            counter.incrementAndGet();
        }

        @Test
        public void m1() {
        }

        @Test
        public void m2() {
        }

        @Test
        public void m3() {
        }

        @Test
        public void m4() {
        }

        @Test
        public void m5() {
        }
    }

    /**
     * Same behavior <tt>new ParallelComputer(true, true)</tt>.
     */
    @Test(timeout = 500)
    public void ordinalParallelComputer() throws Throwable {
        AllDefaultPossibilitiesBuilder builder = new AllDefaultPossibilitiesBuilder(false);

        ParentRunner class1 = (ParentRunner) builder.runnerForClass(C.class);
        class1.setScheduler(createOrdinalScheduler(C.class));

        ParentRunner class2 = (ParentRunner) builder.runnerForClass(C.class);
        class2.setScheduler(createOrdinalScheduler(C.class));

        ParentRunner class3 = (ParentRunner) builder.runnerForClass(C.class);
        class3.setScheduler(createOrdinalScheduler(C.class));

        Suite suite = new Suite(null, Arrays.<Runner>asList(class1, class2, class3)) {};
        suite.setScheduler(createOrdinalScheduler(suite.getClass()));

        suite.run(new RunNotifier());
    }

    private static class Barrier extends Balancer {
        private final AtomicInteger concurrentChildren;
        private volatile int maxConcurrency;

        Barrier(int permits, AtomicInteger concurrentChildren) {
            super(permits);
            this.concurrentChildren = concurrentChildren;
        }

        int maxConcurrentChildren() {
            return maxConcurrency;
        }

        public synchronized void releasePermit() {
            maxConcurrency = Math.max(maxConcurrency, concurrentChildren.getAndDecrement());
            super.releasePermit();
        }
    }

    /**
     * Behavior as maven-surefire configuration would expect:
     * CPU Core = 8: totally 8 threads in one common thread pool.
     * 2 parallel classes in one suite.
     * 6 parallel methods across all classes.
     *
     * Three class runners, five methods in each class.
     */
    @Test(timeout = 500)
    public void classesAndMethodsInCommonPool() throws Throwable {
        final int maxConcurrentClasses = 2;
        final AtomicInteger concurrentClasses = new AtomicInteger(0);
        final Barrier parallelClassesLimit = new Barrier(maxConcurrentClasses, concurrentClasses);

        final ExecutorService pool = Executors.newFixedThreadPool(8);

        Scheduler suiteScheduler =
                new Scheduler(createSuiteDescription(Suite.class), createParallelSharedStrategy(pool), parallelClassesLimit) {

                    @Override protected void beforeScheduling() {
                        concurrentClasses.incrementAndGet();
                    }
                };

        Runner[] classRunners = createClassRunners(suiteScheduler, pool, 3);
        Runner suite = createSuiteRunner(suiteScheduler, classRunners);
        suite.run(new RunNotifier());

        // no class left scheduling
        assertThat(concurrentClasses.get(), is(0));

        // one or two classes executed a time
        assertThat(parallelClassesLimit.maxConcurrentChildren(), anyOf(is(1), is(2)));

        // In total, three classes initialized.
        assertThat(counter.get(), is(3));
    }

    /**
     * Totally 16 threads in one common thread pool;
     * 2 parallel suites.
     * 4 parallel classes across all suites.
     * The rest is 10 parallel methods across all classes.
     *
     * 3 suite runners, 5 class runners, 5 methods in each class.
     * The side effect (expected in maven-surefire) is that the number of threads per methods is not limited and so
     * the previous threads (finished suite, or classes) may be reused and the execution will speed up at the end.
     */
    @Test(timeout = 500)
    public void suitesClassesMethodsInCommonPool() throws Throwable {
        final int classesPerSuite = 5;

        final int maxConcurrentSuites = 2;
        final Balancer parallelSuitesLimit = new Balancer(maxConcurrentSuites);

        final int maxConcurrentClasses = 4;
        final Balancer parallelClassesLimit = new Balancer(maxConcurrentClasses);

        final ExecutorService pool = Executors.newFixedThreadPool(16);

        Description desc = createSuiteDescription(Suite.class);

        Scheduler suitesScheduler =
                new Scheduler(desc, createParallelSharedStrategy(pool), parallelSuitesLimit);

        Scheduler suiteScheduler1 =
                new Scheduler(desc, suitesScheduler, createParallelSharedStrategy(pool), parallelClassesLimit);

        Scheduler suiteScheduler2 =
                new Scheduler(desc, suitesScheduler, createParallelSharedStrategy(pool), parallelClassesLimit);

        Scheduler suiteScheduler3 =
                new Scheduler(desc, suitesScheduler, createParallelSharedStrategy(pool), parallelClassesLimit);

        Runner suite1 = createSuiteRunner(suiteScheduler1, createClassRunners(suiteScheduler1, pool, classesPerSuite));
        Runner suite2 = createSuiteRunner(suiteScheduler2, createClassRunners(suiteScheduler2, pool, classesPerSuite));
        Runner suite3 = createSuiteRunner(suiteScheduler3, createClassRunners(suiteScheduler3, pool, classesPerSuite));

        Runner suite = createSuiteRunner(suitesScheduler, suite1, suite2, suite3);
        suite.run(new RunNotifier());

        // In total, 15 classes initialized.
        assertThat(counter.get(), is(15));
    }

    /**
     * Three thread pools with size:
     * 2 parallel suites.
     * 4 parallel classes across all suites.
     * 10 parallel methods across all classes.
     *
     * 3 suite runners, 5 class runners, 5 methods in each class.
     * Consumes more memory than the previous test.
     */
    @Test(timeout = 500)
    public void suitesClassesMethodsInIndividualPools() throws Throwable {
        final int parallelSuites = 2;
        final int parallelClasses = 4;
        final int parallelMethods = 10;
        final int classesPerSuite = 5;

        ExecutorService suiteThreadPool = Executors.newFixedThreadPool(parallelClasses);
        ExecutorService classesThreadPool = Executors.newFixedThreadPool(parallelMethods);

        Runner suite1 = createSuiteRunner(suiteThreadPool, createClassRunners(classesThreadPool, classesPerSuite));
        Runner suite2 = createSuiteRunner(suiteThreadPool, createClassRunners(classesThreadPool, classesPerSuite));
        Runner suite3 = createSuiteRunner(suiteThreadPool, createClassRunners(classesThreadPool, classesPerSuite));

        Description desc = createSuiteDescription(Suite.class);
        Scheduler suitesScheduler = new Scheduler(desc, createParallelStrategy(parallelSuites));
        Runner suite = createSuiteRunner(suitesScheduler, suite1, suite2, suite3);
        suite.run(new RunNotifier());

        // In total, 15 classes initialized.
        assertThat(counter.get(), is(15));
    }

    /**
     * One infinitive pool.
     * 3 suite runners, 5 class runners, 5 methods in each class.
     */
    @Test(timeout = 500)
    public void suitesClassesMethodsInInfinitivePool() throws Throwable {
        final int classesPerSuite = 5;

        ExecutorService pool = Executors.newCachedThreadPool();

        Runner suite1 = createSuiteRunner(pool, createClassRunners(pool, classesPerSuite));
        Runner suite2 = createSuiteRunner(pool, createClassRunners(pool, classesPerSuite));
        Runner suite3 = createSuiteRunner(pool, createClassRunners(pool, classesPerSuite));

        Description desc = createSuiteDescription(Suite.class);
        Scheduler suitesScheduler = new Scheduler(desc, createParallelSharedStrategy(pool));
        Runner suite = createSuiteRunner(suitesScheduler, suite1, suite2, suite3);
        suite.run(new RunNotifier());

        // In total, 15 classes initialized.
        assertThat(counter.get(), is(15));
    }

    private static Scheduler createOrdinalScheduler(Class clazz) {
        Description description = createSuiteDescription(clazz);
        SchedulingStrategy strategy = createParallelStrategyUnbounded();
        return new Scheduler(description, strategy);
    }

    private static Scheduler createScheduler(Class testClass, Scheduler master, ExecutorService pool) {
        Description description = createSuiteDescription(testClass);
        SchedulingStrategy strategy = createParallelSharedStrategy(pool);
        return new Scheduler(description, master, strategy);
    }

    private static Scheduler createScheduler(Class testClass, ExecutorService pool) {
        Description description = createSuiteDescription(testClass);
        SchedulingStrategy strategy = createParallelSharedStrategy(pool);
        return new Scheduler(description, strategy);
    }

    private static Runner createClassRunner(RunnerBuilder builder, Class test, Scheduler master, ExecutorService pool)
            throws Throwable {
        ParentRunner classRunner = (ParentRunner) builder.runnerForClass(test);
        classRunner.setScheduler(createScheduler(test, master, pool));
        return classRunner;
    }

    private static Runner createClassRunner(RunnerBuilder builder, Class test, ExecutorService pool) throws Throwable {
        ParentRunner classRunner = (ParentRunner) builder.runnerForClass(test);
        classRunner.setScheduler(createScheduler(test, pool));
        return classRunner;
    }

    private static Suite createSuiteRunner(Scheduler scheduler, Runner... runners) throws InitializationError {
        Suite suite = new Suite(null, Arrays.<Runner>asList(runners)) {};
        suite.setScheduler(scheduler);
        return suite;
    }

    private static Suite createSuiteRunner(ExecutorService suiteThreadPool, Runner... runners) throws InitializationError {
        Suite suite = new Suite(null, Arrays.<Runner>asList(runners)) {};
        Description desc = createSuiteDescription(Suite.class);
        SchedulingStrategy strategy = createParallelSharedStrategy(suiteThreadPool);
        suite.setScheduler(new Scheduler(desc, strategy));
        return suite;
    }

    private static Runner[] createClassRunners(Scheduler master, ExecutorService pool, int numClasses) throws Throwable {
        AllDefaultPossibilitiesBuilder builder = new AllDefaultPossibilitiesBuilder(false);
        Runner[] classes = new Runner[numClasses];
        for (int i = 0; i < numClasses; ++i) {
            classes[i] = createClassRunner(builder, C.class, master, pool);
        }
        return classes;
    }

    private static Runner[] createClassRunners(ExecutorService pool, int numClasses) throws Throwable {
        AllDefaultPossibilitiesBuilder builder = new AllDefaultPossibilitiesBuilder(false);
        Runner[] classes = new Runner[numClasses];
        for (int i = 0; i < numClasses; ++i) {
            classes[i] = createClassRunner(builder, C.class, pool);
        }
        return classes;
    }
}
