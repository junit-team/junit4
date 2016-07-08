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
 *     &#064;Rule
 *     public final RuleChain chain = RuleChain
 *             .outerRule(new TestRuleAdapter(new MyMethodRule()))
 *             .around(new TestRuleAdapter(new MyMethodRule()));
 * </pre>
 *
 * @since 4.12
 */
public class TestRuleAdapter implements TestRule {
    private final MethodRule rule;

    public TestRuleAdapter(MethodRule rule) {
        this.rule = rule;
    }

    public Statement apply(Statement base, Description description) {
        return rule.apply(base, createFrameworkMethod(description), getTestObject(description));
    }

    private FrameworkMethod createFrameworkMethod(Description description) {
        try {
            String methodName = description.getMethodName();
            Class<?> c = getTestClass(description);
            Method m = c.getDeclaredMethod(methodName);
            return new FrameworkMethod(m);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Class<?> getTestClass(Description description) {
        return description.getTestClass();
    }

    private Object getTestObject(Description description) {
        try {
            return getTestClass(description).newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
