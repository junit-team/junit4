package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.ParentRunner;
import org.junit.runner.Description;
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
	
	private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
		return annotation.value();
	}

	private final List<Runner> fRunners;

	public Suite(Class<?> klass, SuiteBuilder builder) throws InitializationError {
		// TODO: (Dec 13, 2007 2:33:16 AM) doc difference between each constructor

		this(builder, klass, getAnnotatedClasses(klass));
		validate();
	}

	public Suite(SuiteBuilder requestor, Class<?>[] classes) {
		this(null, requestor.runners(classes));
	}

	protected Suite(SuiteBuilder builder, Class<?> klass, Class<?>[] annotatedClasses) throws InitializationError {
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
