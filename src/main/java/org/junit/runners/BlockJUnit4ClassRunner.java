package org.junit.runners;

import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_VALIDATOR;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.internal.runners.statements.Fail;
import org.junit.internal.runners.statements.FailOnTimeout;
import org.junit.internal.runners.statements.InvokeMethod;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
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
 * <li>It is published, and extension and reuse are encouraged, whereas {@code
 * JUnit4ClassRunner} was in an internal package, and is now deprecated.
 * </ul>
 */
public class BlockJUnit4ClassRunner extends ParentRunner<FrameworkMethod> {
	/**
	 * Creates a BlockJUnit4ClassRunner to run {@code klass}
	 * 
	 * @throws InitializationError
	 *             if the test class is malformed.
	 */
	public BlockJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	//
	// Implementation of ParentRunner
	// 

	@Override
	protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
		Description description= describeChild(method);
		if (method.getAnnotation(Ignore.class) != null) {
			notifier.fireTestIgnored(description);
		} else {
			runLeaf(methodBlock(method), description, notifier);
		}
	}

	@Override
	protected Description describeChild(FrameworkMethod method) {
		return Description.createTestDescription(getTestClass().getJavaClass(),
				testName(method), method.getAnnotations());
	}

	@Override
	protected List<FrameworkMethod> getChildren() {
		return computeTestMethods();
	}

	//
	// Override in subclasses
	//

	/**
	 * Returns the methods that run tests. Default implementation returns all
	 * methods annotated with {@code @Test} on this class and superclasses that
	 * are not overridden.
	 */
	protected List<FrameworkMethod> computeTestMethods() {
		return getTestClass().getAnnotatedMethods(Test.class);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);

		validateNoNonStaticInnerClass(errors);
		validateConstructor(errors);
		validateInstanceMethods(errors);
		validateFields(errors);
	}

	protected void validateNoNonStaticInnerClass(List<Throwable> errors) {
		if (getTestClass().isANonStaticInnerClass()) {
			String gripe= "The inner class " + getTestClass().getName()
					+ " is not static.";
			errors.add(new Exception(gripe));
		}
	}

	/**
	 * Adds to {@code errors} if the test class has more than one constructor,
	 * or if the constructor takes parameters. Override if a subclass requires
	 * different validation rules.
	 */
	protected void validateConstructor(List<Throwable> errors) {
		validateOnlyOneConstructor(errors);
		validateZeroArgConstructor(errors);
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

	/**
	 * Adds to {@code errors} for each method annotated with {@code @Test},
	 * {@code @Before}, or {@code @After} that is not a public, void instance
	 * method with no arguments.
	 * 
	 * @deprecated unused API, will go away in future version
	 */
	@Deprecated
	protected void validateInstanceMethods(List<Throwable> errors) {
		validatePublicVoidNoArgMethods(After.class, false, errors);
		validatePublicVoidNoArgMethods(Before.class, false, errors);
		validateTestMethods(errors);

		if (computeTestMethods().size() == 0)
			errors.add(new Exception("No runnable methods"));
	}

	private void validateFields(List<Throwable> errors) {
		RULE_VALIDATOR.validate(getTestClass(), errors);
	}

	/**
	 * Adds to {@code errors} for each method annotated with {@code @Test}that
	 * is not a public, void instance method with no arguments.
	 */
	protected void validateTestMethods(List<Throwable> errors) {
		validatePublicVoidNoArgMethods(Test.class, false, errors);
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
	 * Returns the name that describes {@code method} for {@link Description}s.
	 * Default implementation is the method's name
	 */
	protected String testName(FrameworkMethod method) {
		return method.getName();
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
	 * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the {@code
	 * expecting} attribute, return normally only if the previous step threw an
	 * exception of the correct type, and throw an exception otherwise.
	 * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the {@code
	 * timeout} attribute, throw an exception if the previous step takes more
	 * than the specified number of milliseconds.
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
	protected Statement methodBlock(FrameworkMethod method) {
		Object test;
		try {
			test= new ReflectiveCallable() {
				@Override
				protected Object runReflectiveCall() throws Throwable {
					return createTest();
				}
			}.run();
		} catch (Throwable e) {
			return new Fail(e);
		}

		Statement statement= methodInvoker(method, test);
		statement= possiblyExpectingExceptions(method, test, statement);
		statement= withPotentialTimeout(method, test, statement);
		statement= withBefores(method, test, statement);
		statement= withAfters(method, test, statement);
		statement= withRules(method, test, statement);
		return statement;
	}

	//
	// Statement builders
	//

	/**
	 * Returns a {@link Statement} that invokes {@code method} on {@code test}
	 */
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		return new InvokeMethod(method, test);
	}

	/**
	 * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
	 * has the {@code expecting} attribute, return normally only if {@code next}
	 * throws an exception of the correct type, and throw an exception
	 * otherwise.
	 * 
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement possiblyExpectingExceptions(FrameworkMethod method,
			Object test, Statement next) {
		Test annotation= method.getAnnotation(Test.class);
		return expectsException(annotation) ? new ExpectException(next,
				getExpectedException(annotation)) : next;
	}

	/**
	 * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
	 * has the {@code timeout} attribute, throw an exception if {@code next}
	 * takes more than the specified number of milliseconds.
	 * 
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement withPotentialTimeout(FrameworkMethod method,
			Object test, Statement next) {
		long timeout= getTimeout(method.getAnnotation(Test.class));
		return timeout > 0 ? new FailOnTimeout(next, timeout) : next;
	}

	/**
	 * Returns a {@link Statement}: run all non-overridden {@code @Before}
	 * methods on this class and superclasses before running {@code next}; if
	 * any throws an Exception, stop execution and pass the exception on.
	 * 
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement withBefores(FrameworkMethod method, Object target,
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
	 * 
	 * @deprecated Will be private soon: use Rules instead
	 */
	@Deprecated
	protected Statement withAfters(FrameworkMethod method, Object target,
			Statement statement) {
		List<FrameworkMethod> afters= getTestClass().getAnnotatedMethods(
				After.class);
		return afters.isEmpty() ? statement : new RunAfters(statement, afters,
				target);
	}

	private Statement withRules(FrameworkMethod method, Object target,
			Statement statement) {
		Statement result= statement;
		result= withMethodRules(method, target, result);
		result= withTestRules(method, target, result);
		return result;
	}

	@SuppressWarnings("deprecation")
	private Statement withMethodRules(FrameworkMethod method, Object target,
			Statement result) {
		List<TestRule> testRules= getTestRules(target);
		for (org.junit.rules.MethodRule each : getMethodRules(target))
			if (! testRules.contains(each))
				result= each.apply(result, method, target);
		return result;
	}

	@SuppressWarnings("deprecation")
	private List<org.junit.rules.MethodRule> getMethodRules(Object target) {
		return rules(target);
	}

	/**
	 * @param target
	 *            the test case instance
	 * @return a list of MethodRules that should be applied when executing this
	 *         test
	 * @deprecated {@link org.junit.rules.MethodRule} is a deprecated interface. Port to
	 *             {@link TestRule} and
	 *             {@link BlockJUnit4ClassRunner#getTestRules(Object)}
	 */
	@Deprecated
	protected List<org.junit.rules.MethodRule> rules(Object target) {
		return getTestClass().getAnnotatedFieldValues(target, Rule.class,
				org.junit.rules.MethodRule.class);
	}

	/**
	 * Returns a {@link Statement}: apply all non-static {@link Value} fields
	 * annotated with {@link Rule}.
	 *
	 * @param statement The base statement
	 * @return a RunRules statement if any class-level {@link Rule}s are
	 *         found, or the base statement
	 */
	private Statement withTestRules(FrameworkMethod method, Object target,
			Statement statement) {
		List<TestRule> testRules= getTestRules(target);
		return testRules.isEmpty() ? statement :
			new RunRules(statement, testRules, describeChild(method));
	}

	/**
	 * @param target
	 *            the test case instance
	 * @return a list of TestRules that should be applied when executing this
	 *         test
	 */
	protected List<TestRule> getTestRules(Object target) {
		return getTestClass().getAnnotatedFieldValues(target,
				Rule.class, TestRule.class);
	}

	private Class<? extends Throwable> getExpectedException(Test annotation) {
		if (annotation == null || annotation.expected() == None.class)
			return null;
		else
			return annotation.expected();
	}

	private boolean expectsException(Test annotation) {
		return getExpectedException(annotation) != null;
	}

	private long getTimeout(Test annotation) {
		if (annotation == null)
			return 0;
		return annotation.timeout();
	}
}
