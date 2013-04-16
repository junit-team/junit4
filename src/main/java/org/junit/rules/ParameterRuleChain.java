package org.junit.rules;

import org.junit.internal.builders.ParameterRunnerBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The ParameterRuleChain parameter rule allows ordering of ParameterRules. You create a
 * {@code ParameterRuleChain} with {@link #outerRule(org.junit.rules.ParameterRule)} and subsequent calls of
 * {@link #around(org.junit.rules.ParameterRule)}:
 *
 * @since 4.13
 */
public class ParameterRuleChain implements ParameterRule {
    private static final ParameterRuleChain EMPTY_CHAIN = new ParameterRuleChain(
            Collections.<ParameterRule>emptyList());

    private List<ParameterRule> rulesStartingWithInnerMost;

    /**
     * Returns a {@code ParameterRuleChain} without a {@link org.junit.rules.ParameterRule}. This method may
     * be the starting point of a {@code ParameterRuleChain}.
     *
     * @return a {@code ParameterRuleChain} without a {@link org.junit.rules.ParameterRule}.
     */
    public static ParameterRuleChain emptyRuleChain() {
        return EMPTY_CHAIN;
    }

    /**
     * Returns a {@code ParmeterRuleChain} with a single {@link org.junit.rules.ParameterRule}. This method
     * is the usual starting point of a {@code ParameterRuleChain}.
     *
     * @param outerRule the outer parameter rule of the {@code ParameterRuleChain}.
     * @return a {@code ParameterRuleChain} with a single {@link org.junit.rules.ParameterRule}.
     */
    public static ParameterRuleChain outerRule(ParameterRule outerRule) {
        return emptyRuleChain().around(outerRule);
    }

    private ParameterRuleChain(List<ParameterRule> rules) {
        this.rulesStartingWithInnerMost = rules;
    }

    /**
     * Create a new {@code ParameterRuleChain}, which encloses the {@code nextRule} with
     * the rules of the current {@code ParamterRuleChain}.
     *
     * @param enclosedRule the rule to enclose.
     * @return a new {@code ParameterRuleChain}.
     */
    public ParameterRuleChain around(ParameterRule enclosedRule) {
        List<ParameterRule> rulesOfNewChain = new ArrayList<ParameterRule>();
        rulesOfNewChain.add(enclosedRule);
        rulesOfNewChain.addAll(rulesStartingWithInnerMost);
        return new ParameterRuleChain(rulesOfNewChain);
    }

    /**
     * {@inheritDoc}
     */
    public ParameterRunnerBuilder apply(ParameterRunnerBuilder base) {
        for (ParameterRule each : rulesStartingWithInnerMost) {
            base = each.apply(base);
        }
        return base;
    }
}