package org.junit.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * The RuleChain rule allows ordering of TestRules. You create a
 * {@code RuleChain} with {@link #outerRule(TestRule)} and subsequent calls of
 * {@link #around(TestRule)}:
 *
 * <pre>
 * public class UseRuleChain {
 * 	&#064;Rule
 * 	public RuleChain chain= RuleChain
 * 	                       .outerRule(new LoggingRule("outer rule"))
 * 	                       .around(new LoggingRule("middle rule"))
 * 	                       .around(new LoggingRule("inner rule"));
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
 * {@code RuleChain} cannot be used to define the order of existing rules.
 * For example in the below snippet the LoggingRule {@code middle} would be
 * executed outside as well as inside the {@code RuleChain}:
 *
 * <pre>
 * &#064;Rule
 * public LoggingRule middle = new LoggingRule("middle rule");
 * 
 * &#064;Rule
 * public RuleChain chain = RuleChain
 *                          .outerRule(new LoggingRule("outer rule"))
 *                          .around(middle)
 *                          .around(new LoggingRule("inner rule"));
 * </pre>
 *
 * @deprecated Since 4.13 ordering of rules can be naturally specified with an annotation attribute.
 * @see org.junit.Rule#order()
 * @see org.junit.ClassRule#order()
 * @since 4.10
 */
@Deprecated
public class RuleChain implements TestRule {
    private static final RuleChain EMPTY_CHAIN = new RuleChain(
            Collections.<TestRule>emptyList());

    private List<TestRule> rulesStartingWithInnerMost;

    /**
     * Returns a {@code RuleChain} without a {@link TestRule}. This method may
     * be the starting point of a {@code RuleChain}.
     *
     * @return a {@code RuleChain} without a {@link TestRule}.
     */
    public static RuleChain emptyRuleChain() {
        return EMPTY_CHAIN;
    }

    /**
     * Returns a {@code RuleChain} with a single {@link TestRule}. This method
     * is the usual starting point of a {@code RuleChain}.
     *
     * @param outerRule the outer rule of the {@code RuleChain}.
     * @return a {@code RuleChain} with a single {@link TestRule}.
     */
    public static RuleChain outerRule(TestRule outerRule) {
        return emptyRuleChain().around(outerRule);
    }

    private RuleChain(List<TestRule> rules) {
        this.rulesStartingWithInnerMost = rules;
    }

    /**
     * Create a new {@code RuleChain}, which encloses the given {@link TestRule} with
     * the rules of the current {@code RuleChain}.
     *
     * @param enclosedRule the rule to enclose; must not be {@code null}.
     * @return a new {@code RuleChain}.
     * @throws NullPointerException if the argument {@code enclosedRule} is {@code null}
     */
    public RuleChain around(TestRule enclosedRule) {
        if (enclosedRule == null) {
            throw new NullPointerException("The enclosed rule must not be null");
        }
        List<TestRule> rulesOfNewChain = new ArrayList<TestRule>();
        rulesOfNewChain.add(enclosedRule);
        rulesOfNewChain.addAll(rulesStartingWithInnerMost);
        return new RuleChain(rulesOfNewChain);
    }

    /**
     * {@inheritDoc}
     */
    public Statement apply(Statement base, Description description) {
        return new RunRules(base, rulesStartingWithInnerMost, description);
    }
}