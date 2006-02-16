package org.junit.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import org.junit.internal.runners.OldTestClassRunner;

/** Runner for use with JUnit 3.8.x-style AllTests classes
 * (those that only implement a static <code>suite()</code>
 * method). For example: <br>
 * <code>
 * &nbsp;@RunWith(AllTests.class)
 * public class ProductTests {
 * &nbsp;&nbsp;public static junit.framework.Test suite() {
 * &nbsp;&nbsp;&nbsp;&nbsp;...
 * &nbsp;&nbsp;}
 * }
 * </code>
 */
public class AllTests extends OldTestClassRunner {
	private static Test suite(Class<?> klass) throws Throwable {
		Method suiteMethod= null;
		Test suite= null;
		try {
			suiteMethod= klass.getMethod("suite");
			if (! Modifier.isStatic(suiteMethod.getModifiers())) {
				throw new Exception(klass.getName() + ".suite() must be static");
			}
			suite= (Test) suiteMethod.invoke(null); // static method
		} catch (InvocationTargetException e) { // TODO need coverage
			throw e.getCause();
		}
		return suite;
	}
	
	@SuppressWarnings("unchecked")
	public AllTests(Class<?> klass) throws Throwable {
		super(suite(klass));
	}
}
