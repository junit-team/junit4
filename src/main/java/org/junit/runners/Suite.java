package org.junit.runners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.rules.MethodRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.tests.experimental.categories.CategoryTest.FilterRule;

/**
 * Using <code>Suite</code> as a runner allows you to manually
 * build a suite containing tests from many classes. It is the JUnit 4 equivalent of the JUnit 3.8.x
 * static {@link junit.framework.Test} <code>suite()</code> method. To use it, annotate a class
 * with <code>@RunWith(Suite.class)</code> and <code>@SuiteClasses(TestClass1.class, ...)</code>.
 * When you run this class, it will run all the tests in all the suite classes.
 */
public class Suite extends ParentRunner<Runner> {
	/**
	 * Returns an empty suite.
	 */
	public static Runner emptySuite() {
		try {
			return new Suite((Class<?>)null, new Class<?>[0]);
		} catch (InitializationError e) {
			throw new RuntimeException("This shouldn't be possible");
		}
	}
	
	/**
	 * The <code>SuiteClasses</code> annotation specifies the classes to be run when a class
	 * annotated with <code>@RunWith(Suite.class)</code> is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public @interface SuiteClasses {
		/**
		 * @return the classes to be run
		 */
		public Class<?>[] value();
	}
	
	private static Class<?>[] getAnnotatedClasses(Class<?> klass) throws InitializationError {
		SuiteClasses annotation= klass.getAnnotation(SuiteClasses.class);
		if (annotation == null)
			throw new InitializationError(String.format("class '%s' must have a SuiteClasses annotation", klass.getName()));
		return annotation.value();
	}

	private final List<Runner> fRunners;

	/**
	 * Called reflectively on classes annotated with <code>@RunWith(Suite.class)</code>
	 * 
	 * @param klass the root class
	 * @param builder builds runners for classes in the suite
	 * @throws InitializationError
	 */
	public Suite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		this(builder, klass, getAnnotatedClasses(klass));
	}

	/**
	 * Call this when there is no single root class (for example, multiple class names
	 * passed on the command line to {@link org.junit.runner.JUnitCore}
	 * 
	 * @param builder builds runners for classes in the suite
	 * @param classes the classes in the suite
	 * @throws InitializationError 
	 */
	public Suite(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
		this(null, builder.runners(null, classes));
	}
	
	/**
	 * Call this when the default builder is good enough. Left in for compatibility with JUnit 4.4.
	 * @param klass the root of the suite
	 * @param suiteClasses the classes in the suite
	 * @throws InitializationError
	 */
	protected Suite(Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
		this(new AllDefaultPossibilitiesBuilder(true), klass, suiteClasses);
	}
	
	/**
	 * Called by this class and subclasses once the classes making up the suite have been determined
	 * 
	 * @param builder builds runners for classes in the suite
	 * @param klass the root of the suite
	 * @param suiteClasses the classes in the suite
	 * @throws InitializationError
	 */
	protected Suite(RunnerBuilder builder, Class<?> klass, Class<?>[] suiteClasses) throws InitializationError {
		this(klass, builder.runners(klass, suiteClasses));
	}
	
	/**
	 * Called by this class and subclasses once the runners making up the suite have been determined
	 * 
	 * @param klass root of the suite
	 * @param runners for each class in the suite, a {@link Runner}
	 * @throws InitializationError 
	 */
	protected Suite(Class<?> klass, List<Runner> runners) throws InitializationError {
		super(klass);
		fRunners = runners;
		try {
			rules(getTestClass().getOnlyConstructor().newInstance());
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the MethodRules that can transform the block
	 * that runs each method in the tested class.
	 */
	protected List<FilterRule> rules(Object test) {
		List<FilterRule> results= new ArrayList<FilterRule>();
		for (FrameworkField each : ruleFields())
			results.add(createRule(test, each));
		return results;
	}

	private List<FrameworkField> ruleFields() {
		return getTestClass().getAnnotatedFields(Rule.class);
	}

	private FilterRule createRule(Object test,
			FrameworkField each) {
		try {
			return (FilterRule) each.get(test);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(
					"How did getFields return a field we couldn't access?");
		}
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
	protected void runChild(Runner runner, final RunNotifier notifier) {
		runner.run(notifier);
	}
}
