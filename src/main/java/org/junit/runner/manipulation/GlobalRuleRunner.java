package org.junit.runner.manipulation;

import java.util.List;

public class GlobalRuleRunner {

    private final List<Class<?>> rules;

    public GlobalRuleRunner(List<Class<?>> rules) {
        this.rules = rules;
    }

    public void apply(Object object) {
        if (object instanceof GlobalRuleRunnable) {
            GlobalRuleRunnable runnable = (GlobalRuleRunnable) object;
            runnable.setGlobalRules(rules);
        }
    }

}
