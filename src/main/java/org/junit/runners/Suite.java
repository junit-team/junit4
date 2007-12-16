package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.ParentRunner;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * Using <code>Suite</code> as a runner allows you to manually
 * build a suite containing tests from many classes. It is the JUnit 4 equivalent of the JUnit 3.8.x
 * static {@link junit.framework.Test} <code>suite()</code> method. To use it, annotate a class
 * with <code>@RunWith(Suite.class)</code> and <code>@SuiteClasses(TestClass1.class, ...)</code>.
 * When you run this class, it will run all the tests in all the suite classes.
 */
public class Suite extends ParentRunner<Runner> {
	/**
	 * The <code>SuiteClasses</code> annotation specifies the classes to be run when a class
	 * annotated with <code>@RunWith(Suite.class)</code> is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface SuiteClasses {
		public Class<?>[] value();
	}
	
	private static class SuiteBuilder {
		private Set<Class<?>> parents = new HashSet<Class<?>>();

		private List<Runner> runners(Class<?> parent, Class<?>[] children)
				throws InitializationError {
			addParent(parent);
			
			try {
				return runners(children);
			} finally {
				removeParent(parent);
			}
		}

		private List<Runner> runners(Class<?>[] children) {
			ArrayList<Runner> runners= new ArrayList<Runner>();
			for (Class<?> each : children) {
				Runner childRunner= Request.aClass(each).getRunner();
				if (childRunner != null)
					runners.add(childRunner);
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

	// To prevent test writers from hanging themselves, we need to shorten the rope we hand them.
	// SuiteBuilder builds a Suite one class at a time, making sure that no Suite contains
	// itself as a direct or indirect child.  Since Suites are constructed through
	// reflective constructor invocations, we have one static builder that is referenced by all.
	// This won't work correctly in the face of concurrency. For that we need to
	// add parameters to getRunner(), which would be much more complicated.
	public static SuiteBuilder builder = new SuiteBuilder();
	
	private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
		return annotation.value();
	}

	private final List<Runner> fRunners;

	public Suite(Class<?> klass) throws InitializationError {
		// TODO: (Dec 13, 2007 2:33:16 AM) doc difference between each constructor

		this(klass, getAnnotatedClasses(klass));
		validate();
	}

	public Suite(Class<?>[] classes) {
		this(null, builder.runners(classes));
	}

	protected Suite(Class<?> klass, Class<?>[] annotatedClasses) throws InitializationError {
		this(klass, builder.runners(klass, annotatedClasses));
	}
	
	protected Suite(Class<?> klass, List<Runner> runners) {
		super(klass);
		fRunners = runners;
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		getTestClass().validateStaticMethods(errors);
	}
	
	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}
	
	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner each, final RunNotifier notifier) {
		each.run(notifier);
	}
}
