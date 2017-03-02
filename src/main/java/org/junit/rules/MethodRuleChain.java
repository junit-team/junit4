package org.junit.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * The MethodRuleChain rule allows ordering of MethodRules. You create a
 * {@code MethodRuleChain} with {@link #outerRule(MethodRule)} and subsequent calls of
 * {@link #around(MethodRule)}:
 *
 * <pre>
 * public static class UseRuleChain {
 * 	&#064;Rule
 * 	public MethodRuleChain chain= MethodRuleChain
 * 	                       .outerRule(new LoggingMethodRule("outer rule")
 * 	                       .around(new LoggingMethodRule("middle rule")
 * 	                       .around(new LoggingMethodRule("inner rule");
 *
 * 	&#064;Test
 * 	public void example() {
 * 		assertTrue(true);
 *     }
 * }
 * </pre>
 *
 * writes the log
 *
 * <pre>
 * starting outer rule
 * starting middle rule
 * starting inner rule
 * finished inner rule
 * finished middle rule
 * finished outer rule
 * </pre>
 *
 */
public class MethodRuleChain implements MethodRule {

	private static final MethodRuleChain EMPTY_CHAIN = new MethodRuleChain(
            Collections.<MethodRule>emptyList());

    private List<MethodRule> rulesStartingWithInnerMost;

    /**
     * Returns a {@code MethodRuleChain} without a {@link MethodRule}. This method may
     * be the starting point of a {@code MethodRuleChain}.
     *
     * @return a {@code MethodRuleChain} without a {@link MethodRule}.
     */
    public static MethodRuleChain emptyMethodRuleChain() {
        return EMPTY_CHAIN;
    }
	
    /**
     * Returns a {@code MethodRuleChain} with a single {@link MethodRule}. This method
     * is the usual starting point of a {@code MethodRuleChain}.
     *
     * @param outerRule the outer rule of the {@code MethodRuleChain}.
     * @return a {@code MethodRuleChain} with a single {@link MethodRule}.
     */
    public static MethodRuleChain outerRule(MethodRule outerRule) {
        return emptyMethodRuleChain().around(outerRule);
    }

    private MethodRuleChain(List<MethodRule> rules) {
        this.rulesStartingWithInnerMost = rules;
    }
    
    /**
     * Create a new {@code RuleChain}, which encloses the {@code nextRule} with
     * the rules of the current {@code RuleChain}.
     *
     * @param enclosedRule the rule to enclose.
     * @return a new {@code RuleChain}.
     */
    public MethodRuleChain around(MethodRule enclosedRule) {
        List<MethodRule> rulesOfNewChain = new ArrayList<MethodRule>();
        rulesOfNewChain.add(enclosedRule);
        rulesOfNewChain.addAll(rulesStartingWithInnerMost);
        return new MethodRuleChain(rulesOfNewChain);
    }
    
    /**
     * {@inheritDoc}
     */
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		for (MethodRule each : rulesStartingWithInnerMost) {
            base = each.apply(base, method, target);
        }
        return base;
	}

}
