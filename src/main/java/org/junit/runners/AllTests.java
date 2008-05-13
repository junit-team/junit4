package org.junit.runners;

import org.junit.internal.runners.SuiteMethod;

/** Runner for use with JUnit 3.8.x-style AllTests classes
 * (those that only implement a static <code>suite()</code>
 * method). For example:
 * <pre>
 * &#064;RunWith(AllTests.class)
 * public class ProductTests {
 *    public static junit.framework.Test suite() {
 *       ...
 *    }
 * }
 * </pre>
 */
public class AllTests extends SuiteMethod {
	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public AllTests(Class<?> klass) throws Throwable {
		super(klass);
	}
}
