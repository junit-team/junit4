package org.junit.runners;

import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_VALIDATOR;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.TestFactories;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.FrameworkTest;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * Implements the JUnit 4 standard test case class model, as defined by the
 * annotations in the org.junit package. Many users will never notice this
 * class: it is now the default test class runner, but it should have exactly
 * the same behavior as the old test class runner ({@code JUnit4ClassRunner}).
 * 
 * BlockJUnit4ClassRunner has advantages for writers of custom JUnit runners
 * that are slight changes to the default behavior, however:
 * 
 * <ul>
 * <li>It has a much simpler implementation based on {@link Statement}s,
 * allowing new operations to be inserted into the appropriate point in the
 * execution flow.
 * 
 * <li>It is published, and extension and reuse are encouraged, whereas
 * {@code JUnit4ClassRunner} was in an internal package, and is now deprecated.
 * </ul>
 */
public class CustomizableJUnit4ClassRunner extends ParentRunner<FrameworkTest> {

	private final List<FrameworkTest> allTests= new ArrayList<FrameworkTest>();

	/**
	 * Creates a BlockJUnit4ClassRunner to run {@code klass}
	 * 
	 * @throws InitializationError
	 *             if the test class is malformed.
	 */
	public CustomizableJUnit4ClassRunner(Class<?> klass)
			throws InitializationError {
		this(klass, discoverTestFactories(klass));
	}

	protected static List<TestFactory> discoverTestFactories(Class<?> klass)
			throws InitializationError {
		List<TestFactory> result= new ArrayList<TestFactory>();

		TestFactories annotation= klass.getAnnotation(TestFactories.class);
		if (annotation != null) {
			try {
				for (Class<? extends TestFactory> testFactoryClass : annotation
						.values()) {
					result.add(testFactoryClass.newInstance());
				}
			} catch (Throwable e) {
				throw new InitializationError(e);
			}
		}

		if (result.isEmpty()) {
			result.add(new ReflectionTestFactory());
		}
		return result;
	}

	protected CustomizableJUnit4ClassRunner(Class<?> klass,
			List<TestFactory> testFactories) throws InitializationError {
		super(klass);

		List<Throwable> errors= new ArrayList<Throwable>();

		for (TestFactory testFactory : testFactories) {
			allTests.addAll(testFactory.computeTestMethods(getTestClass(),
					errors));
		}

		collectInitializationErrors2(errors);
	}

	/**
	 * Disable validation started by parent constructor
	 */
	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
	}

	protected void collectInitializationErrors2(List<Throwable> errors)
			throws InitializationError {
		super.collectInitializationErrors(errors);

		validateNoNonStaticInnerClass(errors);
		validateOnlyOneConstructor(errors);
		validateZeroArgConstructor(errors);
		validatePublicVoidNoArgMethods(After.class, false, errors);
		validatePublicVoidNoArgMethods(Before.class, false, errors);
		validateFields(errors);
		validateMethods(errors);

		if (allTests.isEmpty()) {
			errors.add(new Exception("No runnable methods"));
		}
		if (!errors.isEmpty()) {
			throw new InitializationError(errors);
		}
	}

	protected void validateNoNonStaticInnerClass(List<Throwable> errors) {
		if (getTestClass().isANonStaticInnerClass()) {
			String gripe= "The inner class " + getTestClass().getName()
					+ " is not static.";
			errors.add(new Exception(gripe));
		}
	}

	/**
	 * Adds to {@code errors} if the test class has more than one constructor
	 * (do not override)
	 */
	protected void validateOnlyOneConstructor(List<Throwable> errors) {
		if (!hasOneConstructor()) {
			String gripe= "Test class should have exactly one public constructor";
			errors.add(new Exception(gripe));
		}
	}

	/**
	 * Adds to {@code errors} if the test class's single constructor takes
	 * parameters (do not override)
	 */
	protected void validateZeroArgConstructor(List<Throwable> errors) {
		if (!getTestClass().isANonStaticInnerClass()
				&& hasOneConstructor()
				&& (getTestClass().getOnlyConstructor().getParameterTypes().length != 0)) {
			String gripe= "Test class should have exactly one public zero-argument constructor";
			errors.add(new Exception(gripe));
		}
	}

	private boolean hasOneConstructor() {
		return getTestClass().getJavaClass().getConstructors().length == 1;
	}

	protected void validateFields(List<Throwable> errors) {
		RULE_VALIDATOR.validate(getTestClass(), errors);
	}

	protected void validateMethods(List<Throwable> errors) {
		RULE_METHOD_VALIDATOR.validate(getTestClass(), errors);
	}

	//
	// Implementation of ParentRunner
	//

	@Override
	protected List<FrameworkTest> getChildren() {
		return allTests;
	}

	@Override
	protected Description describeChild(FrameworkTest method) {
		return method.createDescription(getTestClass());
	}

	@Override
	protected void runChild(final FrameworkTest method, RunNotifier notifier) {
		Description description= describeChild(method);
		if (method.shouldBeIgnored()) {
			notifier.fireTestIgnored(description);
		} else {
			Statement statement= methodBlock(method);
			runLeaf(statement, description, notifier);
		}
	}

	/**
	 * Returns a new fixture for running a test. Default implementation executes
	 * the test class's no-argument constructor (validation should have ensured
	 * one exists).
	 */
	protected Object createTest() throws Exception {
		return getTestClass().getOnlyConstructor().newInstance();
	}

	/**
	 * Returns a Statement that, when executed, either returns normally if
	 * {@code method} passes, or throws an exception if {@code method} fails.
	 * 
	 * Here is an outline of the default implementation:
	 * 
	 * <ul>
	 * <li>Invoke {@code method} on the result of {@code createTest()}, and
	 * throw any exceptions thrown by either operation.
	 * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the
	 * {@code expecting} attribute, return normally only if the previous step
	 * threw an exception of the correct type, and throw an exception otherwise.
	 * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the
	 * {@code timeout} attribute, throw an exception if the previous step takes
	 * more than the specified number of milliseconds.
	 * <li>ALWAYS run all non-overridden {@code @Before} methods on this class
	 * and superclasses before any of the previous steps; if any throws an
	 * Exception, stop execution and pass the exception on.
	 * <li>ALWAYS run all non-overridden {@code @After} methods on this class
	 * and superclasses after any of the previous steps; all After methods are
	 * always executed: exceptions thrown by previous steps are combined, if
	 * necessary, with exceptions from After methods into a
	 * {@link MultipleFailureException}.
	 * <li>ALWAYS allow {@code @Rule} fields to modify the execution of the
	 * above steps. A {@code Rule} may prevent all execution of the above steps,
	 * or add additional behavior before and after, or modify thrown exceptions.
	 * For more information, see {@link TestRule}
	 * </ul>
	 * 
	 * This can be overridden in subclasses, either by overriding this method,
	 * or the implementations creating each sub-statement.
	 */
	protected Statement methodBlock(FrameworkTest method) {
		Object test;
		try {
			test= createTest();
		} catch (InvocationTargetException e) {
			return new Fail(e.getTargetException());
		} catch (Throwable e) {
			return new Fail(e);
		}

		Statement statement= testBlock(method, test);
		statement= withBefores(method, test, statement);
		statement= withAfters(method, test, statement);
		statement= withRules(method, test, statement);
		return statement;
	}

	protected Statement testBlock(FrameworkTest method, Object test) {
		return method.createStatement(test);
	}

	/**
	 * Returns a {@link Statement}: run all non-overridden {@code @Before}
	 * methods on this class and superclasses before running {@code next}; if
	 * any throws an Exception, stop execution and pass the exception on.
	 */
	private Statement withBefores(FrameworkTest method, Object target,
			Statement statement) {
		List<FrameworkMethod> befores= getTestClass().getAnnotatedMethods(
				Before.class);
		return befores.isEmpty() ? statement : new RunBefores(statement,
				befores, target);
	}

	/**
	 * Returns a {@link Statement}: run all non-overridden {@code @After}
	 * methods on this class and superclasses before running {@code next}; all
	 * After methods are always executed: exceptions thrown by previous steps
	 * are combined, if necessary, with exceptions from After methods into a
	 * {@link MultipleFailureException}.
	 */
	private Statement withAfters(FrameworkTest method, Object target,
			Statement statement) {
		List<FrameworkMethod> afters= getTestClass().getAnnotatedMethods(
				After.class);
		return afters.isEmpty() ? statement : new RunAfters(statement, afters,
				target);
	}

	protected Statement withRules(FrameworkTest method, Object target,
			Statement statement) {
		List<TestRule> testRules= getTestRules(target);
		return withTestRules(method, testRules, target, statement);
	}

	/**
	 * Returns a {@link Statement}: apply all non-static {@link Value} fields
	 * annotated with {@link Rule}.
	 * 
	 * @param method
	 * @param testRules
	 * @param statement
	 *            The base statement
	 * @return a RunRules statement if any class-level {@link Rule}s are found,
	 *         or the base statement
	 */
	protected Statement withTestRules(FrameworkTest method,
			List<TestRule> testRules, Object target, Statement statement) {
		return testRules.isEmpty() ? statement : new RunRules(statement,
				testRules, describeChild(method));
	}

	/**
	 * @param target
	 *            the test case instance
	 * @return a list of TestRules that should be applied when executing this
	 *         test
	 */
	protected List<TestRule> getTestRules(Object target) {
		List<TestRule> result= getTestClass().getAnnotatedMethodValues(target,
				Rule.class, TestRule.class);

		result.addAll(getTestClass().getAnnotatedFieldValues(target,
				Rule.class, TestRule.class));

		return result;
	}
}
