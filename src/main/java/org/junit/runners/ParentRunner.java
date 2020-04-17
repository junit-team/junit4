package org.junit.runners;

import static org.junit.internal.Checks.notNull;
import static org.junit.internal.runners.rules.RuleMemberValidator.CLASS_RULE_METHOD_VALIDATOR;
import static org.junit.internal.runners.rules.RuleMemberValidator.CLASS_RULE_VALIDATOR;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.rules.RunRules;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Orderer;
import org.junit.runner.manipulation.InvalidOrderingException;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Orderable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.FrameworkMember;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.InvalidTestClassError;
import org.junit.runners.model.MemberValueConsumer;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.junit.validator.AnnotationsValidator;
import org.junit.validator.TestClassValidator;

/**
 * Provides most of the functionality specific to a Runner that implements a
 * "parent node" in the test tree, with children defined by objects of some data
 * type {@code T}. (For {@link BlockJUnit4ClassRunner}, {@code T} is
 * {@link Method} . For {@link Suite}, {@code T} is {@link Class}.) Subclasses
 * must implement finding the children of the node, describing each child, and
 * running each child. ParentRunner will filter and sort children, handle
 * {@code @BeforeClass} and {@code @AfterClass} methods,
 * handle annotated {@link ClassRule}s, create a composite
 * {@link Description}, and run children sequentially.
 *
 * @since 4.5
 */
public abstract class ParentRunner<T> extends Runner implements Filterable,
        Orderable {
    private static final List<TestClassValidator> VALIDATORS = Collections.<TestClassValidator>singletonList(
            new AnnotationsValidator());

    private final Lock childrenLock = new ReentrantLock();
    private final TestClass testClass;

    // Guarded by childrenLock
    private volatile List<T> filteredChildren = null;

    private volatile RunnerScheduler scheduler = new RunnerScheduler() {
        public void schedule(Runnable childStatement) {
            childStatement.run();
        }

        public void finished() {
            // do nothing
        }
    };

    /**
     * Constructs a new {@code ParentRunner} that will run {@code @TestClass}
     */
    protected ParentRunner(Class<?> testClass) throws InitializationError {
        this.testClass = createTestClass(testClass);
        validate();
    }

   /**
    * Constructs a new {@code ParentRunner} that will run the {@code TestClass}.
    *
    * @since 4.13
    */
    protected ParentRunner(TestClass testClass) throws InitializationError {
       this.testClass = notNull(testClass);
       validate();
    }

    /**
     * @deprecated Please use {@link #ParentRunner(org.junit.runners.model.TestClass)}.
     * @since 4.12
     */
    @Deprecated
    protected TestClass createTestClass(Class<?> testClass) {
        return new TestClass(testClass);
    }

    //
    // Must be overridden
    //

    /**
     * Returns a list of objects that define the children of this Runner.
     */
    protected abstract List<T> getChildren();

    /**
     * Returns a {@link Description} for {@code child}, which can be assumed to
     * be an element of the list returned by {@link ParentRunner#getChildren()}
     */
    protected abstract Description describeChild(T child);

    /**
     * Runs the test corresponding to {@code child}, which can be assumed to be
     * an element of the list returned by {@link ParentRunner#getChildren()}.
     * Subclasses are responsible for making sure that relevant test events are
     * reported through {@code notifier}
     */
    protected abstract void runChild(T child, RunNotifier notifier);

    //
    // May be overridden
    //

    /**
     * Adds to {@code errors} a throwable for each problem noted with the test class (available from {@link #getTestClass()}).
     * Default implementation adds an error for each method annotated with
     * {@code @BeforeClass} or {@code @AfterClass} that is not
     * {@code public static void} with no arguments.
     */
    protected void collectInitializationErrors(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(BeforeClass.class, true, errors);
        validatePublicVoidNoArgMethods(AfterClass.class, true, errors);
        validateClassRules(errors);
        applyValidators(errors);
    }

    private void applyValidators(List<Throwable> errors) {
        if (getTestClass().getJavaClass() != null) {
            for (TestClassValidator each : VALIDATORS) {
                errors.addAll(each.validateTestClass(getTestClass()));
            }
        }
    }

    /**
     * Adds to {@code errors} if any method in this class is annotated with
     * {@code annotation}, but:
     * <ul>
     * <li>is not public, or
     * <li>takes parameters, or
     * <li>returns something other than void, or
     * <li>is static (given {@code isStatic is false}), or
     * <li>is not static (given {@code isStatic is true}).
     * </ul>
     */
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
            boolean isStatic, List<Throwable> errors) {
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);

        for (FrameworkMethod eachTestMethod : methods) {
            eachTestMethod.validatePublicVoidNoArg(isStatic, errors);
        }
    }

    private void validateClassRules(List<Throwable> errors) {
        CLASS_RULE_VALIDATOR.validate(getTestClass(), errors);
        CLASS_RULE_METHOD_VALIDATOR.validate(getTestClass(), errors);
    }

    /**
     * Constructs a {@code Statement} to run all of the tests in the test class.
     * Override to add pre-/post-processing. Here is an outline of the
     * implementation:
     * <ol>
     * <li>Determine the children to be run using {@link #getChildren()}
     * (subject to any imposed filter and sort).</li>
     * <li>If there are any children remaining after filtering and ignoring,
     * construct a statement that will:
     * <ol>
     * <li>Apply all {@code ClassRule}s on the test-class and superclasses.</li>
     * <li>Run all non-overridden {@code @BeforeClass} methods on the test-class
     * and superclasses; if any throws an Exception, stop execution and pass the
     * exception on.</li>
     * <li>Run all remaining tests on the test-class.</li>
     * <li>Run all non-overridden {@code @AfterClass} methods on the test-class
     * and superclasses: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.</li>
     * </ol>
     * </li>
     * </ol>
     *
     * @return {@code Statement}
     */
    protected Statement classBlock(final RunNotifier notifier) {
        Statement statement = childrenInvoker(notifier);
        if (!areAllChildrenIgnored()) {
            statement = withBeforeClasses(statement);
            statement = withAfterClasses(statement);
            statement = withClassRules(statement);
            statement = withInterruptIsolation(statement);
        }
        return statement;
    }

    private boolean areAllChildrenIgnored() {
        for (T child : getFilteredChildren()) {
            if (!isIgnored(child)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a {@link Statement}: run all non-overridden {@code @BeforeClass} methods on this class
     * and superclasses before executing {@code statement}; if any throws an
     * Exception, stop execution and pass the exception on.
     */
    protected Statement withBeforeClasses(Statement statement) {
        List<FrameworkMethod> befores = testClass
                .getAnnotatedMethods(BeforeClass.class);
        return befores.isEmpty() ? statement :
                new RunBefores(statement, befores, null);
    }

    /**
     * Returns a {@link Statement}: run all non-overridden {@code @AfterClass} methods on this class
     * and superclasses after executing {@code statement}; all AfterClass methods are
     * always executed: exceptions thrown by previous steps are combined, if
     * necessary, with exceptions from AfterClass methods into a
     * {@link org.junit.runners.model.MultipleFailureException}.
     */
    protected Statement withAfterClasses(Statement statement) {
        List<FrameworkMethod> afters = testClass
                .getAnnotatedMethods(AfterClass.class);
        return afters.isEmpty() ? statement :
                new RunAfters(statement, afters, null);
    }

    /**
     * Returns a {@link Statement}: apply all
     * static fields assignable to {@link TestRule}
     * annotated with {@link ClassRule}.
     *
     * @param statement the base statement
     * @return a RunRules statement if any class-level {@link Rule}s are
     *         found, or the base statement
     */
    private Statement withClassRules(Statement statement) {
        List<TestRule> classRules = classRules();
        return classRules.isEmpty() ? statement :
                new RunRules(statement, classRules, getDescription());
    }

    /**
     * @return the {@code ClassRule}s that can transform the block that runs
     *         each method in the tested class.
     */
    protected List<TestRule> classRules() {
        ClassRuleCollector collector = new ClassRuleCollector();
        testClass.collectAnnotatedMethodValues(null, ClassRule.class, TestRule.class, collector);
        testClass.collectAnnotatedFieldValues(null, ClassRule.class, TestRule.class, collector);
        return collector.getOrderedRules();
    }

    /**
     * Returns a {@link Statement}: Call {@link #runChild(Object, RunNotifier)}
     * on each object returned by {@link #getChildren()} (subject to any imposed
     * filter and sort)
     */
    protected Statement childrenInvoker(final RunNotifier notifier) {
        return new Statement() {
            @Override
            public void evaluate() {
                runChildren(notifier);
            }
        };
    }

    /**
     * @return a {@link Statement}: clears interrupt status of current thread after execution of statement
     */
    protected final Statement withInterruptIsolation(final Statement statement) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } finally {
                    Thread.interrupted(); // clearing thread interrupted status for isolation
                }
            }
        };
    }

    /**
     * Evaluates whether a child is ignored. The default implementation always
     * returns <code>false</code>.
     * 
     * <p>{@link BlockJUnit4ClassRunner}, for example, overrides this method to
     * filter tests based on the {@link Ignore} annotation.
     */
    protected boolean isIgnored(T child) {
        return false;
    }

    private void runChildren(final RunNotifier notifier) {
        final RunnerScheduler currentScheduler = scheduler;
        try {
            for (final T each : getFilteredChildren()) {
                currentScheduler.schedule(new Runnable() {
                    public void run() {
                        ParentRunner.this.runChild(each, notifier);
                    }
                });
            }
        } finally {
            currentScheduler.finished();
        }
    }

    /**
     * Returns a name used to describe this Runner
     */
    protected String getName() {
        return testClass.getName();
    }

    //
    // Available for subclasses
    //

    /**
     * Returns a {@link TestClass} object wrapping the class to be executed.
     */
    public final TestClass getTestClass() {
        return testClass;
    }

    /**
     * Runs a {@link Statement} that represents a leaf (aka atomic) test.
     */
    protected final void runLeaf(Statement statement, Description description,
            RunNotifier notifier) {
        EachTestNotifier eachNotifier = new EachTestNotifier(notifier, description);
        eachNotifier.fireTestStarted();
        try {
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            eachNotifier.addFailedAssumption(e);
        } catch (Throwable e) {
            eachNotifier.addFailure(e);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    /**
     * @return the annotations that should be attached to this runner's
     *         description.
     */
    protected Annotation[] getRunnerAnnotations() {
        return testClass.getAnnotations();
    }

    //
    // Implementation of Runner
    //

    @Override
    public Description getDescription() {
        Class<?> clazz = getTestClass().getJavaClass();
        Description description;
        // if subclass overrides `getName()` then we should use it
        // to maintain backwards compatibility with JUnit 4.12
        if (clazz == null || !clazz.getName().equals(getName())) {
            description = Description.createSuiteDescription(getName(), getRunnerAnnotations());
        } else {
            description = Description.createSuiteDescription(clazz, getRunnerAnnotations());
        }

        for (T child : getFilteredChildren()) {
            description.addChild(describeChild(child));
        }
        return description;
    }

    @Override
    public void run(final RunNotifier notifier) {
        EachTestNotifier testNotifier = new EachTestNotifier(notifier,
                getDescription());
        testNotifier.fireTestSuiteStarted();
        try {
            Statement statement = classBlock(notifier);
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            testNotifier.addFailedAssumption(e);
        } catch (StoppedByUserException e) {
            throw e;
        } catch (Throwable e) {
            testNotifier.addFailure(e);
        } finally {
            testNotifier.fireTestSuiteFinished();
        }
    }

    //
    // Implementation of Filterable and Sortable
    //

    public void filter(Filter filter) throws NoTestsRemainException {
        childrenLock.lock();
        try {
            List<T> children = new ArrayList<T>(getFilteredChildren());
            for (Iterator<T> iter = children.iterator(); iter.hasNext(); ) {
                T each = iter.next();
                if (shouldRun(filter, each)) {
                    try {
                        filter.apply(each);
                    } catch (NoTestsRemainException e) {
                        iter.remove();
                    }
                } else {
                    iter.remove();
                }
            }
            filteredChildren = Collections.unmodifiableList(children);
            if (filteredChildren.isEmpty()) {
                throw new NoTestsRemainException();
            }
        } finally {
            childrenLock.unlock();
        }
    }

    public void sort(Sorter sorter) {
        if (shouldNotReorder()) {
            return;
        }

        childrenLock.lock();
        try {
            for (T each : getFilteredChildren()) {
                sorter.apply(each);
            }
            List<T> sortedChildren = new ArrayList<T>(getFilteredChildren());
            Collections.sort(sortedChildren, comparator(sorter));
            filteredChildren = Collections.unmodifiableList(sortedChildren);
        } finally {
            childrenLock.unlock();
        }
    }

    /**
     * Implementation of {@link Orderable#order(Orderer)}.
     *
     * @since 4.13
     */
    public void order(Orderer orderer) throws InvalidOrderingException {
        if (shouldNotReorder()) {
            return;
        }

        childrenLock.lock();
        try {
            List<T> children = getFilteredChildren();
            // In theory, we could have duplicate Descriptions. De-dup them before ordering,
            // and add them back at the end.
            Map<Description, List<T>> childMap = new LinkedHashMap<Description, List<T>>(
                    children.size());
            for (T child : children) {
                Description description = describeChild(child);
                List<T> childrenWithDescription = childMap.get(description);
                if (childrenWithDescription == null) {
                    childrenWithDescription = new ArrayList<T>(1);
                    childMap.put(description, childrenWithDescription);
                }
                childrenWithDescription.add(child);
                orderer.apply(child);
            }

            List<Description> inOrder = orderer.order(childMap.keySet());

            children = new ArrayList<T>(children.size());
            for (Description description : inOrder) {
                children.addAll(childMap.get(description));
            }
            filteredChildren = Collections.unmodifiableList(children);
        } finally {
            childrenLock.unlock();
        }
    }

    //
    // Private implementation
    //

    private boolean shouldNotReorder() {
        // If the test specifies a specific order, do not reorder.
        return getDescription().getAnnotation(FixMethodOrder.class) != null;
    }

    private void validate() throws InitializationError {
        List<Throwable> errors = new ArrayList<Throwable>();
        collectInitializationErrors(errors);
        if (!errors.isEmpty()) {
            throw new InvalidTestClassError(testClass.getJavaClass(), errors);
        }
    }

    private List<T> getFilteredChildren() {
        if (filteredChildren == null) {
            childrenLock.lock();
            try {
                if (filteredChildren == null) {
                    filteredChildren = Collections.unmodifiableList(
                            new ArrayList<T>(getChildren()));
                }
            } finally {
                childrenLock.unlock();
            }
        }
        return filteredChildren;
    }

    private boolean shouldRun(Filter filter, T each) {
        return filter.shouldRun(describeChild(each));
    }

    private Comparator<? super T> comparator(final Sorter sorter) {
        return new Comparator<T>() {
            public int compare(T o1, T o2) {
                return sorter.compare(describeChild(o1), describeChild(o2));
            }
        };
    }

    /**
     * Sets a scheduler that determines the order and parallelization
     * of children.  Highly experimental feature that may change.
     */
    public void setScheduler(RunnerScheduler scheduler) {
        this.scheduler = scheduler;
    }

    private static class ClassRuleCollector implements MemberValueConsumer<TestRule> {
        final List<RuleContainer.RuleEntry> entries = new ArrayList<RuleContainer.RuleEntry>();

        public void accept(FrameworkMember<?> member, TestRule value) {
            ClassRule rule = member.getAnnotation(ClassRule.class);
            entries.add(new RuleContainer.RuleEntry(value, RuleContainer.RuleEntry.TYPE_TEST_RULE,
                    rule != null ? rule.order() : null));
        }

        public List<TestRule> getOrderedRules() {
            Collections.sort(entries, RuleContainer.ENTRY_COMPARATOR);
            List<TestRule> result = new ArrayList<TestRule>(entries.size());
            for (RuleContainer.RuleEntry entry : entries) {
                result.add((TestRule) entry.rule);
            }
            return result;
        }
    }
}
