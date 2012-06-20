package org.junit.experimental.runners;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.junit.runners.Suite;
import org.junit.runners.model.RunnerBuilder;


/**
 * If you put tests in inner classes, Ant, for example, won't find them. By running the outer class
 * with Enclosed, the tests in the inner classes will be run. You might put tests in inner classes
 * to group them for convenience or to share constants.
 * 
 *  So, for example:
 *  <pre>
 *  \@RunWith(Enclosed.class)
 *  public class ListTests {
 *  	...useful shared stuff...
 *  	public static class OneKindOfListTest {...}
 *  	public static class AnotherKind {...}
 *  }
 *  </pre>
 *  
 *  For a real example, @see org.junit.tests.manipulation.SortableTest.
 */
public class Enclosed extends Suite {
	/**
	 * Only called reflectively. Do not use programmatically.
	 */
	public Enclosed(Class<?> klass, RunnerBuilder builder) throws Throwable {
		super(builder, klass, filterOutAbstractClasses(klass.getClasses()));
	}
	
	private static Class<?>[] filterOutAbstractClasses(final Class<?>[] classes) {		
		final Set<Class<?>> filteredSet = new HashSet<Class<?>>();

		for (final Class<?> clazz : classes) {
			if (!Modifier.isAbstract(clazz.getModifiers())) {
				filteredSet.add(clazz);
			}
		}
		
		return filteredSet.toArray(new Class<?>[] { });
	}	
}
