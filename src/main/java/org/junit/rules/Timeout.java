/**
 * 
 */
package org.junit.rules;

import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * The Timeout Rule applies the same timeout to all test methods in a class:
 * 
 * <pre>
 * public static class HasGlobalTimeout {
 * 	public static String log;
 * 
 * 	&#064;Rule
 * 	public MethodRule globalTimeout= new Timeout(20);
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
public class Timeout implements MethodRule {
	private final int fMillis;

	/**
	 * @param millis the millisecond timeout
	 */
	public Timeout(int millis) {
		fMillis= millis;
	}

	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		return new FailOnTimeout(base, fMillis);
	}
}