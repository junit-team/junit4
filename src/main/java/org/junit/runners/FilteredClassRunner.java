package org.junit.runners;

import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Implements a test filtering decorator around a BlockJUnit4ClassRunner.
 */
public class FilteredClassRunner extends BlockJUnit4ClassRunner {
    private final BlockJUnit4ClassRunner underlyingRunner;
    private final Filter filter;

    public FilteredClassRunner(final BlockJUnit4ClassRunner underlyingRunner, final Class<?> klass, final Filter filter)
            throws InitializationError {
        super(klass);

        this.underlyingRunner = (underlyingRunner == null)
                ? new BlockJUnit4ClassRunner(klass)
                : underlyingRunner;
        this.filter = filter;
    }

    @Override
    public void runChild(final FrameworkMethod method, final RunNotifier notifier) {
        final Description description = describeChild(method);

        if (!filter.shouldRun(description)) {
            notifier.fireTestIgnored(description);
        } else {
            underlyingRunner.runChild(method, notifier);
        }
    }

    @Override
    protected String getName() {
        return underlyingRunner.getName();
    }

    @Override
    protected Description describeChild(final FrameworkMethod method) {
        return underlyingRunner.describeChild(method);
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        return underlyingRunner.getChildren();
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return underlyingRunner.computeTestMethods();
    }

    @Override
    protected void collectInitializationErrors(final List<Throwable> errors) {
        // underlyingRunner should never be null, if this check is removed, for some reason AllTests fails with NPE
        if (underlyingRunner != null) {
            underlyingRunner.collectInitializationErrors(errors);
        }
    }

    @Override
    protected void validateNoNonStaticInnerClass(final List<Throwable> errors) {
        underlyingRunner.validateNoNonStaticInnerClass(errors);
    }

    @Override
    protected void validateConstructor(final List<Throwable> errors) {
        underlyingRunner.validateConstructor(errors);
    }

    @Override
    protected void validateOnlyOneConstructor(final List<Throwable> errors) {
        underlyingRunner.validateOnlyOneConstructor(errors);
    }

    @Override
    protected void validateZeroArgConstructor(final List<Throwable> errors) {
        underlyingRunner.validateZeroArgConstructor(errors);
    }

    @Override
    protected void validateFields(final List<Throwable> errors) {
        underlyingRunner.validateFields(errors);
    }

    @Override
    protected void validateTestMethods(final List<Throwable> errors) {
        underlyingRunner.validateTestMethods(errors);
    }

    @Override
    protected Object createTest() throws Exception {
        return underlyingRunner.createTest();
    }

    @Override
    protected String testName(final FrameworkMethod method) {
        return underlyingRunner.testName(method);
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod method) {
        return underlyingRunner.methodBlock(method);
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method, Object test) {
        return underlyingRunner.methodInvoker(method, test);
    }

    @Override
    protected Statement withBefores(final FrameworkMethod method, Object target, Statement statement) {
        return underlyingRunner.withBefores(method, target, statement);
    }

    @Override
    protected Statement withAfters(final FrameworkMethod method, Object target, Statement statement) {
        return underlyingRunner.withAfters(method, target, statement);
    }

    @Override
    protected List<org.junit.rules.MethodRule> rules(final Object target) {
        return underlyingRunner.rules(target);
    }

    @Override
    protected List<TestRule> getTestRules(final Object target) {
        return underlyingRunner.getTestRules(target);
    }

    @Override
    @Deprecated
    protected void validateInstanceMethods(final List<Throwable> errors) {
        underlyingRunner.validateInstanceMethods(errors);
    }

    @Override
    @Deprecated
    protected Statement possiblyExpectingExceptions(final FrameworkMethod method, Object test, Statement next) {
        return underlyingRunner.possiblyExpectingExceptions(method, test, next);
    }

    @Override
    @Deprecated
    protected Statement withPotentialTimeout(final FrameworkMethod method, Object test, Statement next) {
        return underlyingRunner.withPotentialTimeout(method, test, next);
    }
}
