package org.junit.runner.manipulation;

import java.util.List;

/**
 * Runners that allow global {@link org.junit.rules.TestRule} should implement this interface.
 * Implement {@link #setGlobalRules(List)}} to receive the list of classes assignable to
 * {@link org.junit.rules.TestRule} to apply on every test.
 *
 * @since 4.13
 */
public interface GlobalRuleRunnable {

    /**
     * Instantiate and apply global {@link org.junit.rules.TestRule}s to every test.
     *
     * @param rules the list of classes assignable to {@link org.junit.rules.TestRule} to instantiate
     * @throws Exception if any class does not meet {@link org.junit.runner.Runner}'s requirements
     */
    void setGlobalRules(List<Class<?>> rules) throws Exception;

}
