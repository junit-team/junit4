package org.junit.rules;

import static org.junit.rules.RuleChain.emptyRuleChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
 * Stores a list of TestRule-priorities-pairs
 * and can return a lists of all TestRules
 * as well as a list containing the autoconstructed RuleChain.
 */
public class SortRules {
    private final List<RulePriorityPair> rules = new ArrayList<RulePriorityPair>();

    /*
     * Adds a TestRulePriorityPair into the List.
     * @param rule: the TestRule to be added to SortRules
     * @param priority: the annotated priority of the TestRule
     */
    public void add(TestRule rule, int priority) {
        rules.add(new RulePriorityPair(rule,priority));
    }

    /*
     * Returns the a list of all the stored TestRules in insertion order.
     * @return A list of TestRules in the order of their insertion.
     */
    public List<TestRule> listWithAllRules() {
        List<TestRule> result = new ArrayList<TestRule>(rules.size());
        for (RulePriorityPair rule : rules) {
            result.add(rule.testRule);
        }
        return result;
    }

    /*
     * Construct a list of all TestRules with negative priorities
     * plus a autoconstructed RuleChain containing all TestRules with non-negative priorities.
     * The TestRules of the RuleChain are sorted in such a way
     * that the TestRule with the highest priority becomes the most outer TestRule.
     */
    public List<TestRule> listWithAutoconstructedRuleChain() {
        List<TestRule> sortedRules = new ArrayList<TestRule>();
        Integer[] index = new Integer[rules.size()];
        for( int i = 0 ; i < index.length; i++ ) {
            index[i] = i;
        }
        Arrays.sort(index, new Comparator<Integer>() {
            public int compare(Integer i1, Integer i2) {
                return rules.get(i1).compareTo(rules.get(i2));
            }
        });
        RuleChain chain = emptyRuleChain();
        for( int i: index) {
            if (rules.get(i).priority >= 0) {
                chain = chain.around(rules.get(i).testRule);
            } else {
                sortedRules.add(rules.get(i).testRule);
            }
        }
        if (!chain.equals(emptyRuleChain())) {
            sortedRules.add(chain);
        }
        return sortedRules;
    }

    private class RulePriorityPair implements Comparable<RulePriorityPair>{
        private final TestRule testRule;
        private final int      priority;

        private RulePriorityPair(TestRule testRule, int priority) {
            this.testRule = testRule;
            this.priority = priority;
        }

        public int compareTo(RulePriorityPair o) {
            if (this.priority < 0 && o.priority < 0) {
                return 0;
            }
            return o.priority - this.priority;
        }
    }
}
