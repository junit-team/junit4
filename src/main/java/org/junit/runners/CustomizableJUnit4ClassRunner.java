package org.junit.runners;

import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_VALIDATOR;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.AfterRule;
import org.junit.rules.BeforeRule;
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
		super(klass);
		init(discoverTestFactories(klass));
	}

	protected List<TestFactory> discoverTestFactories(Class<?> klass)
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
		init(testFactories);
	}

	private void init(List<TestFactory> testFactories)
			throws InitializationError {
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
		return method.createDescription();
	}

	@Override
	protected void runChild(final FrameworkTest frameworkTest, RunNotifier notifier) {
		Description description= describeChild(frameworkTest);
		if (frameworkTest.isIgnored()) {
			notifier.fireTestIgnored(description);
		} else {
			Statement statement= methodBlock(frameworkTest);
			runLeaf(statement, description, notifier);
		}
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
	protected Statement methodBlock(FrameworkTest frameworkTest) {
		Object testInstance;
		try {
			testInstance= createTest();
		} catch (InvocationTargetException e) {
			return new Fail(e.getTargetException());
		} catch (Throwable e) {
			return new Fail(e);
		}

		List<TestRule> testRules= getTestRules(frameworkTest, testInstance);
		return frameworkTest.createStatement(testInstance, testRules);
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
	 * @param frameworkTest
	 *            TODO
	 * @param testInstance
	 *            the test case instance
	 * @return a list of TestRules that should be applied when executing this
	 *         test
	 */
	protected List<TestRule> getTestRules(FrameworkTest frameworkTest,
			Object testInstance) {
		List<TestRule> testRules= new ArrayList<TestRule>();

		List<FrameworkMethod> befores= getTestClass().getAnnotatedMethods(
				Before.class);
		Collections.reverse(befores);
		for (FrameworkMethod before : befores) {
			testRules.add(new BeforeRule(before, testInstance));
		}

		List<FrameworkMethod> afters= getTestClass().getAnnotatedMethods(
				After.class);
		for (FrameworkMethod after : afters) {
			testRules.add(new AfterRule(after, testInstance));
		}

		testRules.addAll(getTestClass().getAnnotatedMethodValues(testInstance,
				Rule.class, TestRule.class));
		testRules.addAll(getTestClass().getAnnotatedFieldValues(testInstance,
				Rule.class, TestRule.class));

		return testRules;
	}
}
