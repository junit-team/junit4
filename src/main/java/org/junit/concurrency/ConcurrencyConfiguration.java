package org.junit.concurrency;

import java.lang.reflect.Method;

/**
 * <p>This method keeps the {@link ConcurrencyJunitRunner} clean from the
 * load configuration process. See {@link Concurrency @Concurrency} for the
 * available annotation parameters.</p>
 * 
 * <p>There are also a number of system parameters which will be loaded from
 * the envirement with the java calls <code>System.getProperty</code> and
 * <code>System.getenv</code>. Set these parameters (default, min. and max.
 * values for the annotation properties times and parallelThreads) with
 * <code>java -Doptionname=value</code> or
 * <code>export optionname=value</code>.</p>
 * 
 * <p>{@link Concurrency @Concurrency} annotation will be loaded from the test
 * class and all super classes!</p>
 * 
 * <p>The values could be overriden by the test method. Annotation will NOT
 * loaded from overridden methods!</p>
 * 
 * @author Christoph Jerolimov
 */
public class ConcurrencyConfiguration {
	public static final int TIMES_DEFAULT = 10;
	public static final String TIMES_DEFAULT_KEY = "junit.concurreny.times.default";
	public static final String TIMES_MULTIPLICATOR_KEY = "junit.concurreny.times.multiplicator";
	public static final String TIMES_MIN_KEY = "junit.concurreny.times.minimum";
	public static final String TIMES_MAX_KEY = "junit.concurreny.times.maximum";

	public static final int THREADS_DEFAULT = 16;
	public static final String THREADS_DEFAULT_KEY = "junit.concurreny.threads.default";
	public static final String THREADS_MULTIPLICATOR_KEY = "junit.concurreny.threads.multiplicator";
	public static final String THREADS_MIN_KEY = "junit.concurreny.threads.minimum";
	public static final String THREADS_MAX_KEY = "junit.concurreny.threads.maximum";

	private final int classTimes;
	private final int classThreads;
	private final int timesMultiplicator;
	private final int threadMultiplicator;
	
	/**
	 * Create configuration for one junit test class. Load the 
	 * 
	 * @param testClass
	 */
	public ConcurrencyConfiguration(Class<?> testClass) {
		Concurrency classAnnotation = testClass.getAnnotation(Concurrency.class);
		while (!testClass.equals(Object.class) && classAnnotation == null) {
			testClass = testClass.getSuperclass();
			classAnnotation = testClass.getAnnotation(Concurrency.class);
		}
		if (classAnnotation != null) {
			classTimes = getDefault(classAnnotation.times(), TIMES_DEFAULT);
			classThreads = getDefault(classAnnotation.parallelThreads(), THREADS_DEFAULT);
		} else {
			classTimes = getDefault(TIMES_DEFAULT_KEY, TIMES_DEFAULT);
			classThreads = getDefault(THREADS_DEFAULT_KEY, THREADS_DEFAULT);
		}
		timesMultiplicator = getDefault(TIMES_MULTIPLICATOR_KEY, 1);
		threadMultiplicator = getDefault(THREADS_MULTIPLICATOR_KEY, 1);
	}
	
	/**
	 * Return the number of times the junit test method will be invoked.
	 * 
	 * @param testMethod
	 * @return
	 */
	public int getMethodTimes(Method testMethod) {
		Concurrency methodAnnotation = testMethod.getAnnotation(Concurrency.class);
		int methodTimes = classTimes;
		if (methodAnnotation != null && methodAnnotation.times() > 0) {
			methodTimes = testMethod.getAnnotation(Concurrency.class).times();
		}
		return normalizeTimes(methodTimes * timesMultiplicator);
	}
	
	/**
	 * Return the number of threads the {@link ConcurrencyJunitRunner} will
	 * be created for the parallel test run.
	 * 
	 * @param testMethod
	 * @return
	 */
	public int getMethodThreads(Method testMethod) {
		Concurrency methodAnnotation = testMethod.getAnnotation(Concurrency.class);
		int methodThreads = classThreads;
		if (methodAnnotation != null && methodAnnotation.parallelThreads() > 0) {
			methodThreads = testMethod.getAnnotation(Concurrency.class).parallelThreads();
		}
		return normalizeThreads(methodThreads * threadMultiplicator);
	}
	
	/**
	 * Return the value if it is greater than zero (= not the default value).
	 * @param value
	 * @param fallback
	 * @return
	 */
	private int getDefault(int value, int fallback) {
		return value > 0 ? value : fallback;
	}
	
	/**
	 * Return the system property oder system environment variable for the
	 * given key or the fallback value if the value is null.
	 * 
	 * @param key
	 * @param fallback
	 * @return
	 */
	private int getDefault(String key, int fallback) {
		String defaultValue = System.getProperty(key, System.getenv(key));
		return defaultValue != null ? Integer.parseInt(defaultValue) : fallback;
	}
	
	/**
	 * Respecting different hardware requirements the minimum and maximum of
	 * test execution times and the parallel threads could be configured with
	 * system properties or with the system environment.
	 * 
	 * @param in
	 * @param minKey
	 * @param maxKey
	 * @return
	 */
	private int normalize(int in, String minKey, String maxKey) {
		// TODO values cachen?
		String minValue = System.getProperty(minKey, System.getenv(minKey));
		String maxValue = System.getProperty(maxKey, System.getenv(maxKey));
		int min = minValue != null ? Integer.parseInt(minValue) : -1;
		int max = maxValue != null ? Integer.parseInt(maxValue) : -1;
		if (min > 0 && in < min) {
			return min;
		} else if (max > 0 && in > max) {
			return max;
		} else {
			return in;
		}
	}	

	/**
	 * Respecting different hardware requirements the minimum and maximum of
	 * test execution times could be configured with
	 * system properties or with the system environment.
	 * 
	 * @param times
	 * @return
	 */
	private int normalizeTimes(int times) {
		return normalize(times, TIMES_MIN_KEY, TIMES_MAX_KEY);
	}
	
	/**
	 * Respecting different hardware requirements the minimum and maximum of
	 * parallel threads could be configured with
	 * system properties or with the system environment.
	 * 
	 * @param threads
	 * @return
	 */
	private int normalizeThreads(int threads) {
		return normalize(threads, THREADS_MIN_KEY, THREADS_MAX_KEY);
	}
}
