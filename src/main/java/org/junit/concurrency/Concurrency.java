package org.junit.concurrency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Configuration annotation for an Junit test class or method which should
 * be run in a parallel testcase! See {@link ConcurrencyJunitRunner} for more
 * information.</p>
 * 
 * @author Christoph Jerolimov
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Concurrency {
	/*
	 * Defines if the junit test could be reused for all threads or must be
	 * instantiated (with setup and teardown) for each test method.
	 */
// TODO not implemented yet	boolean singleTestInstance = false;
	
	/**
	 * Number of times a single test method will be called.
	 */
    int times() default -1;
    /**
     * Number of threads which will be created for each test method.
     */
    int parallelThreads() default -1;

    /**
     * Expected exceptions means "allowed" exceptions and, at least, one of
     * these exceptions must be thrown in one thread.
     */
	Class<? extends Throwable>[] expectAtLeast() default {};
	
	/**
	 * Set to 1 if your test allowed exception ({@link #expectAtLeast()} and
	 * requires at least one (or more) successful runs. For example for
	 * test the correct result for sychronized methods.
	 */
	int expectMinimumSuccessRuns() default -1;
	/**
	 * Could define the maximum successful runs if you expect at least one
	 * (more more) exceptions with the {@link #expectAtLeast()} parameter.
	 * @return
	 */
	int expectMaximumSuccessRuns() default -1;
	
// TODO not implemented yet	Class<? extends Throwable>[] expectAllOf() default {};
}
