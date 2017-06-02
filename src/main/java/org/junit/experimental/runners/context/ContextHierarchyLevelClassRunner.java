package org.junit.experimental.runners.context;

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.runners.context.statements.RunHierarchicalAfters;
import org.junit.experimental.runners.context.statements.RunHierarchicalBefores;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.internal.runners.statements.Fail;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.internal.runners.rules.RuleFieldValidator.RULE_VALIDATOR;

class ContextHierarchyLevelClassRunner extends BlockJUnit4ClassRunner {
    public ContextHierarchyLevelClassRunner(Class<?> tClass) throws InitializationError {
        super(tClass);
    }

    @Override
    protected void collectInitializationErrors(List<Throwable> errors) {
        if (!getTestClass().getJavaClass().isMemberClass()) {
            super.collectInitializationErrors(errors);
        } else {
            validateOnlyOneConstructor(errors);
            validateContextClassConstructor(errors);
            validateInstanceMethods(errors);
            RULE_VALIDATOR.validate(getTestClass(), errors);
        }
    }

    /**
     * Adds to {@code errors} if the test context class's single constructor takes
     * any other parameter count than one, or if the parameter's type differs from
     * the enclosing class.
     */
    protected final void validateContextClassConstructor(List<Throwable> errors) {
        Class<?> enclosingClass = getTestClass().getJavaClass().getEnclosingClass();
        Class<?>[] parameterTypes = getTestClass().getOnlyConstructor().getParameterTypes();

        if (parameterTypes.length != 1) {
            String gripe = "Test context should have exactly one public one-argument constructor!";
            errors.add(new Exception(gripe));
        } else if (enclosingClass != null && !enclosingClass.isAssignableFrom(parameterTypes[0])) {
            String gripe = "Context constructor must take an instance of the enclosing class!";
            errors.add(new Exception(gripe));
        }
    }

    @Override
    protected String getName() {
        return getTestClass().getJavaClass().getSimpleName();
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        return childrenInvoker(notifier);
    }

    protected Statement methodBlock(FrameworkMethod method) {
        LinkedList<Object> testInstances;
        try {
            testInstances = (LinkedList<Object>) new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createHierarchicalTestInstances();
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }

        Object test = testInstances.getLast();
        Statement statement = methodInvoker(method, test);
        statement = possiblyExpectingExceptions(method, test, statement);
        statement = withPotentialTimeout(method, test, statement);
        statement = withAllBeforeMethods(statement, testInstances);
        statement = withAllAfterMethods(statement, testInstances);
        statement = withTestRules(method, test, statement);
        return statement;
    }

    /**
     * Returns a {@link org.junit.runners.model.Statement}: apply all non-static value fields
     * annotated with {@link org.junit.Rule}.
     *
     * @param statement The base statement
     * @return a RunRules statement if any class-level {@link org.junit.Rule}s are
     *         found, or the base statement
     */
    private Statement withTestRules(FrameworkMethod method, Object target, Statement statement) {
        List<TestRule> testRules = getTestRules(target);
        return testRules.isEmpty() ? statement :
                new RunRules(statement, testRules, describeChild(method));
    }

    /**
     * Returns new fixtures for the entire class hierarchy for running a test.
     * Default implementation executes the test class's no-argument constructor
     * and the inner class default constructor taking the outer class' instance
     * (validation should have ensured one exists).
     */
    protected LinkedList<Object> createHierarchicalTestInstances() throws Exception {
        final Stack<Class<?>> classHierarchy = getClassHierarchy();
        final LinkedList<Object> instances = new LinkedList<Object>();

        // Top level class has empty constructor
        instances.add(classHierarchy.pop().newInstance());

        // Inner class constructors require the enclosing instance
        while (!classHierarchy.empty()) {
            final Object enclosingInstance = instances.getLast();
            final Class<?> innerClass = classHierarchy.pop();
            instances.add(createInnerInstance(enclosingInstance, innerClass));
        }
        return instances;
    }

    private Stack<Class<?>> getClassHierarchy() {
        final Stack<Class<?>> classHierarchy = new Stack<Class<?>>();
        for (Class<?> currentClass = getTestClass().getJavaClass();
             currentClass != null;
             currentClass = currentClass.getEnclosingClass())
            classHierarchy.push(currentClass);
        return classHierarchy;
    }

    private Object createInnerInstance(Object outerInstance, Class<?> innerClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Class<?> outerClass = outerInstance.getClass();
        final Constructor<?> innerConstructor = innerClass.getConstructor(outerClass);
        return innerConstructor.newInstance(outerInstance);
    }

    protected Statement withAllBeforeMethods(final Statement next, final List<Object> targets) {
        final Map<Object, List<FrameworkMethod>> beforeMethods = new LinkedHashMap<Object, List<FrameworkMethod>>();
        for (int i = 0; i < targets.size(); i++) {
            final Object target = targets.get(i);
            beforeMethods.put(target, new TestClass(target.getClass()).getAnnotatedMethods(Before.class));
        }
        return new RunHierarchicalBefores(next, beforeMethods);
    }

    protected Statement withAllAfterMethods(final Statement next, final List<Object> targets) {
        final Map<Object, List<FrameworkMethod>> afterMethods = new LinkedHashMap<Object, List<FrameworkMethod>>();
        for (int i = targets.size() - 1; i >= 0; i--) {
            final Object target = targets.get(i);
            afterMethods.put(target, new TestClass(target.getClass()).getAnnotatedMethods(After.class));
        }
        return new RunHierarchicalAfters(next, afterMethods);
    }
}
