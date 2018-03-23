package org.junit.runner.manipulation;

import java.util.List;

/**
 * A <code>GlobalRuleRunner</code> passes global {@link org.junit.rules.TestRule} to the {@link org.junit.runner.Runner}.
 * In general you will not need to use a <code>GlobalRuleRunner</code> directly. Instead,
 * use {@link org.junit.runner.Request#withGlobalRules(GlobalRuleRunner)}.
 *
 * @since 4.13
 */
public class GlobalRuleRunner {

    private final List<Class<?>> rules;

    public GlobalRuleRunner(List<Class<?>> rules) {
        this.rules = rules;
    }

    /**
     * Passes the list of classes assignable from {@link org.junit.rules.TestRule} to the <code>runner</code>
     */
    public void apply(Object object) throws Exception {
        if (object instanceof GlobalRuleRunnable) {
            GlobalRuleRunnable runnable = (GlobalRuleRunnable) object;
            runnable.setGlobalRules(rules);
        }
    }

}
