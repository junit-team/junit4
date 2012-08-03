/**
 * 
 */
package org.junit.rules;

import java.util.concurrent.TimeUnit;

import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The Timeout Rule applies the same timeout to all test methods in a class:
 * 
 * <pre>
 * public static class HasGlobalTimeout {
 * 	public static String log;
 * 
 * 	&#064;Rule
 * 	public TestRule globalTimeout= new Timeout(20);
 * 
 * 	&#064;Test
 * 	public void testInfiniteLoop1() {
 * 		log+= &quot;ran1&quot;;
 * 		for (;;) {
 * 		}
 * 	}
 * 
 * 	&#064;Test
 * 	public void testInfiniteLoop2() {
 * 		log+= &quot;ran2&quot;;
 * 		for (;;) {
 * 		}
 * 	}
 * }
 * </pre>
 */
public class Timeout implements TestRule {
	private final long fMillis;

	/**
	 * Create a {@code Timeout} instance with the timeout specified
	 * in milliseconds.
	 * 
	 * @param millis the maximum time in milliseconds to allow the
	 * 	      test to run before it should timeout
	 */
	public Timeout(int millis) {
		fMillis= millis;
	}

	/**
	 * Create a {@code Timeout} instance with the timeout specified
	 * at the unit of granularity of the provided {@code TimeUnit}.
	 * 
	 * @param timeout the maximum time to allow the test to run
	 *        before it should timeout
	 * @param unit the time unit of the {@code timeout} argument
	 */
	public Timeout(long timeout, TimeUnit unit) {
		fMillis= unit.toMillis(timeout);
	}
	
	public Statement apply(Statement base, Description description) {
		return new FailOnTimeout(base, fMillis);
	}
}