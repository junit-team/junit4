package org.junit.rules;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * This rule can be use to build another rule, but only just before using it.
 * This can be used to support rule that need data provided by another rule (and
 * these data are only available after the initialization of the other rule).
 * 
 * @author borettim
 * 
 * @param <T>
 */
public abstract class DeferredRule<T extends TestRule> implements TestRule {
    /**
     * Implements this method to build the rule.
     * 
     * @return the rule
     */
    protected abstract T build();

    /*
     * (non-Javadoc)
     * 
     * @see org.junit.rules.TestRule#apply(org.junit.runners.model.Statement,
     * org.junit.runner.Description)
     */
    public Statement apply(Statement base, Description description) {
        return statement(base, description);
    }

    private T rule;

    /**
     * Retrieve the rule that has been used.
     * 
     * @return the rule
     */
    public T getRule() {
        return rule;
    }

    private Statement statement(final Statement base,
            final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                rule= build();
                rule.apply(base, description).evaluate();
            }
        };
    }

}


