package org.junit.runners;

import static org.junit.internal.runners.rules.RuleMemberValidator.RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleMemberValidator.RULE_VALIDATOR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

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
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMember;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MemberValueConsumer;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.junit.validator.PublicClassValidator;
import org.junit.validator.TestClassValidator;

/**
 * Implements the JUnit 4 standard test case class model, as defined by the
 * annotations in the org.junit package. Many users will never notice this
 * class: it is now the default test class runner, but it should have exactly
 * the same behavior as the old test class runner ({@code JUnit4ClassRunner}).
 * <p>
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
 * <p>
 * In turn, in 2009 we introduced {@link Rule}s.  In many cases where extending
 * BlockJUnit4ClassRunner was necessary to add new behavior, {@link Rule}s can
 * be used, which makes the extension more reusable and composable.
 *
 * @since 4.5
 */
public class BlockJUnit4ClassRunner extends ParentRunner<FrameworkMethod> {
    private static TestClassValidator PUBLIC_CLASS_VALIDATOR = new PublicClassValidator();

    private final ConcurrentMap<FrameworkMethod, Description> methodDescriptions = new ConcurrentHashMap<FrameworkMethod, Description>();

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code testClass}
     *
     * @throws InitializationError if the test class is malformed.
     */
    public BlockJUnit4ClassRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code testClass}.
     *
     * @throws InitializationError if the test class is malformed.
     * @since 4.13
     */
    protected BlockJUnit4ClassRunner(TestClass testClass) throws InitializationError {
        super(testClass);
    }

    //
    // Implementation of ParentRunner
    //

    @Override
    protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        if (isIgnored(method)) {
            notifier.fireTestIgnored(description);
        } else {
            Statement statement = new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    methodBlock(method).evaluate();
                }
            };
            runLeaf(statement, description, notifier);
        }
    }

    /**
     * Evaluates whether {@link FrameworkMethod}s are ignored based on the
     * {@link Ignore} annotation.
     */
    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        return child.getAnnotation(Ignore.class) != null;
    }

    @Override
    protected Description describeChild(FrameworkMethod method) {
        Description description = methodDescriptions.get(method);

        if (description == null) {
            description = Description.createTestDescription(getTestClass().getJavaClass(),
                    testName(method), method.getAnnotations());
            methodDescriptions.putIfAbsent(method, description);
        }

        return description;
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

        validatePublicConstructor(errors);
        validateNoNonStaticInnerClass(errors);
        validateConstructor(errors);
        validateInstanceMethods(errors);
        validateFields(errors);
        validateMethods(errors);
    }

    private void validatePublicConstructor(List<Throwable> errors) {
        if (getTestClass().getJavaClass() != null) {
            errors.addAll(PUBLIC_CLASS_VALIDATOR.validateTestClass(getTestClass()));
        }
    }

    protected void validateNoNonStaticInnerClass(List<Throwable> errors) {
        if (getTestClass().isANonStaticInnerClass()) {
            String gripe = "The inner class " + getTestClass().getName()
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
            String gripe = "Test class should have exactly one public constructor";
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
            String gripe = "Test class should have exactly one public zero-argument constructor";
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
     * @deprecated
     */
    @Deprecated
    protected void validateInstanceMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(After.class, false, errors);
        validatePublicVoidNoArgMethods(Before.class, false, errors);
        validateTestMethods(errors);

        if (computeTestMethods().isEmpty()) {
            errors.add(new Exception("No runnable methods"));
        }
    }

    protected void validateFields(List<Throwable> errors) {
        RULE_VALIDATOR.validate(getTestClass(), errors);
    }

    private void validateMethods(List<Throwable> errors) {
        RULE_METHOD_VALIDATOR.validate(getTestClass(), errors);
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
     * Returns a new fixture to run a particular test {@code method} against.
     * Default implementation executes the no-argument {@link #createTest()} method.
     *
     * @since 4.13
     */
    protected Object createTest(FrameworkMethod method) throws Exception {
        return createTest();
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
     * <li>Invoke {@code method} on the result of {@link #createTest(org.junit.runners.model.FrameworkMethod)}, and
     * throw any exceptions thrown by either operation.
     * <li>HOWEVER, if {@code method}'s {@code @Test} annotation has the {@link Test#expected()}
     * attribute, return normally only if the previous step threw an
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
    protected Statement methodBlock(final FrameworkMethod method) {
        Object test;
        try {
            test = new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createTest(method);
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }

        Statement statement = methodInvoker(method, test);
        statement = possiblyExpectingExceptions(method, test, statement);
        statement = withPotentialTimeout(method, test, statement);
        statement = withBefores(method, test, statement);
        statement = withAfters(method, test, statement);
        statement = withRules(method, test, statement);
        statement = withInterruptIsolation(statement);
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
     * has the {@link Test#expected()} attribute, return normally only if {@code next}
     * throws an exception of the correct type, and throw an exception
     * otherwise.
     */
    protected Statement possiblyExpectingExceptions(FrameworkMethod method,
            Object test, Statement next) {
        Test annotation = method.getAnnotation(Test.class);
        Class<? extends Throwable> expectedExceptionClass = getExpectedException(annotation);
        return expectedExceptionClass != null ? new ExpectException(next, expectedExceptionClass) : next;
    }

    /**
     * Returns a {@link Statement}: if {@code method}'s {@code @Test} annotation
     * has the {@code timeout} attribute, throw an exception if {@code next}
     * takes more than the specified number of milliseconds.
     * @deprecated
     */
    @Deprecated
    protected Statement withPotentialTimeout(FrameworkMethod method,
            Object test, Statement next) {
        long timeout = getTimeout(method.getAnnotation(Test.class));
        if (timeout <= 0) {
            return next;
        }
        return FailOnTimeout.builder()
               .withTimeout(timeout, TimeUnit.MILLISECONDS)
               .build(next);
    }

    /**
     * Returns a {@link Statement}: run all non-overridden {@code @Before}
     * methods on this class and superclasses before running {@code next}; if
     * any throws an Exception, stop execution and pass the exception on.
     */
    protected Statement withBefores(FrameworkMethod method, Object target,
            Statement statement) {
        List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(
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
    protected Statement withAfters(FrameworkMethod method, Object target,
            Statement statement) {
        List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
                After.class);
        return afters.isEmpty() ? statement : new RunAfters(statement, afters,
                target);
    }

    private Statement withRules(FrameworkMethod method, Object target, Statement statement) {
        RuleContainer ruleContainer = new RuleContainer();
        CURRENT_RULE_CONTAINER.set(ruleContainer);
        try {
            List<TestRule> testRules = getTestRules(target);
            for (MethodRule each : rules(target)) {
                if (!(each instanceof TestRule && testRules.contains(each))) {
                    ruleContainer.add(each);
                }
            }
            for (TestRule rule : testRules) {
                ruleContainer.add(rule);
            }
        } finally {
            CURRENT_RULE_CONTAINER.remove();
        }
        return ruleContainer.apply(method, describeChild(method), target, statement);
    }

    /**
     * @param target the test case instance
     * @return a list of MethodRules that should be applied when executing this
     *         test
     */
    protected List<MethodRule> rules(Object target) {
        RuleCollector<MethodRule> collector = new RuleCollector<MethodRule>();
        getTestClass().collectAnnotatedMethodValues(target, Rule.class, MethodRule.class,
                collector);
        getTestClass().collectAnnotatedFieldValues(target, Rule.class, MethodRule.class,
                collector);
        return collector.result;
    }

    /**
     * @param target the test case instance
     * @return a list of TestRules that should be applied when executing this
     *         test
     */
    protected List<TestRule> getTestRules(Object target) {
        RuleCollector<TestRule> collector = new RuleCollector<TestRule>();
        getTestClass().collectAnnotatedMethodValues(target, Rule.class, TestRule.class, collector);
        getTestClass().collectAnnotatedFieldValues(target, Rule.class, TestRule.class, collector);
        return collector.result;
    }

    private Class<? extends Throwable> getExpectedException(Test annotation) {
        if (annotation == null || annotation.expected() == None.class) {
            return null;
        } else {
            return annotation.expected();
        }
    }

    private long getTimeout(Test annotation) {
        if (annotation == null) {
            return 0;
        }
        return annotation.timeout();
    }

    private static final ThreadLocal<RuleContainer> CURRENT_RULE_CONTAINER =
            new ThreadLocal<RuleContainer>();

    private static class RuleCollector<T> implements MemberValueConsumer<T> {
        final List<T> result = new ArrayList<T>();

        public void accept(FrameworkMember<?> member, T value) {
            Rule rule = member.getAnnotation(Rule.class);
            if (rule != null) {
                RuleContainer container = CURRENT_RULE_CONTAINER.get();
                if (container != null) {
                    container.setOrder(value, rule.order());
                }
            }
            result.add(value);
        }
    }
}
