package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.internal.runners.CompositeRunner;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Request;
import org.junit.runner.Runner;

/**
 * Using <code>Suite</code> as a runner allows you to manually
 * build a suite containing tests from many classes. It is the JUnit 4 equivalent of the JUnit 3.8.x
 * static {@link junit.framework.Test} <code>suite()</code> method. To use it, annotate a class
 * with <code>@RunWith(Suite.class)</code> and <code>@SuiteClasses(TestClass1.class, ...)</code>.
 * When you run this class, it will run all the tests in all the suite classes.
 */
public class Suite extends CompositeRunner {
	/**
	 * The <code>SuiteClasses</code> annotation specifies the classes to be run when a class
	 * annotated with <code>@RunWith(Suite.class)</code> is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteClasses {
		public Class<?>[] value();
	}

	/**
	 * Internal use only.
	 */
	public Suite(Class<?> klass) throws InitializationError {
		this(klass, getAnnotatedClasses(klass));
	}

	// To prevent test writers from hanging themselves, we need to shorten the rope we hand them.
	// SuiteBuilder builds a Suite one class at a time, making sure that no Suite contains
	// itself as a direct or indirect child.  Since Suites are constructed through
	// reflective constructor invocations, we have one static builder that is referenced by all.
	// This won't work correctly in the face of concurrency. For that we need to
	// add parameters to getRunner(), which would be much more complicated.
	public static SuiteBuilder builder = new SuiteBuilder();
	
	private static class SuiteBuilder {
		private Set<Class<?>> parents = new HashSet<Class<?>>();

		private List<Runner> runners(Class<?> klass, Class<?>[] annotatedClasses)
				throws InitializationError {
			ArrayList<Runner> runners= new ArrayList<Runner>();
			addParent(klass);
			
			// TODO: (Dec 10, 2007 1:10:20 PM) Does this duplicate code from ClassesRequest?
			try {
				for (Class<?> each : annotatedClasses) {
					Runner childRunner= Request.aClass(each).getRunner();
					if (childRunner != null)
						runners.add(childRunner);
				}
			} finally {
				removeParent(klass);
			}
			return runners;
		}

		private Class<?> addParent(Class<?> parent) throws InitializationError {
			if (!parents.add(parent))
				throw new InitializationError(String.format("class '%s' (possibly indirectly) contains itself as a SuiteClass", parent.getName()));
			return parent;
		}
		
		private void removeParent(Class<?> klass) {
			parents.remove(klass);
		}
		
		@Override
		public String toString() {
			return parents.toString();
		}
	}
	
	protected Suite(Class<?> klass, Class<?>[] annotatedClasses) throws InitializationError {
		super(klass, klass.getName(), builder.runners(klass, annotatedClasses));
		List<Throwable> errors= new ArrayList<Throwable>();
		getTestClass().validateStaticMethods(errors);
		assertValid(errors);
	}

	private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
		return annotation.value();
	}
}
