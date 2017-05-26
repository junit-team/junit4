package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;

/**
 * A TestRuleAdapter allows to wrap {@link MethodRule} classes in
 * {@link TestRule} classes in order to use them inside {@link RuleChain}.
 * Assuming we have an existing method rule:
 *
 * <pre>
 * class MyMethodRule implements MethodRule {
 *     public Statement apply(final Statement base, FrameworkMethod method,
 *                            Object target) {
 *         return new Statement() {
 *             &#064;Override
 *             public void evaluate() throws Throwable {
 *                 base.evaluate();
 *             }
 *         };
 *     }
 * }
 * </pre>
 *
 * It can then be used in a {@link RuleChain} like this:
 * <pre>
 * class MyTest {
 *     &#064;Rule
 *     public final RuleChain chain = RuleChain
 *             .outerRule(new TestRuleAdapter(new MyMethodRule(), this))
 *             .around(new TestRuleAdapter(new MyMethodRule(), this));
 *
 *     ...
 * }
 * </pre>
 *
 * @since 4.12
 */
class TestRuleAdapter implements TestRule {
    private final MethodRule rule;
    private final Object testObject;

    /**
     * Creates a new instance wrapping the provided {@link MethodRule}.
     *
     * Needs an instance of the test class using this TestRuleAdapter or
     * the surrounding {@link RuleChain} to be allow the wrapped
     * {@link MethodRule} to interact with it.
     *
     * @param rule the MethodRule to wrap
     * @param testObject instance of the test class
     */
    public TestRuleAdapter(MethodRule rule, Object testObject) {
        this.rule = rule;
        this.testObject = testObject;
    }

    public Statement apply(Statement base, Description description) {
        try {
            String methodName = description.getMethodName();
            Method method = testObject.getClass().getDeclaredMethod(methodName);
            return rule.apply(base, new FrameworkMethod(method), testObject);
        } catch (final NoSuchMethodException e) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    throw e;
                }
            };
        }
    }
}
