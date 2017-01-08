package org.junit.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Stores TestRules with their names and annotated around parameters
 * and can return them in the order specified by the around parameters.
 */
public class SortRules {
    private final Map<String,SortRule> rules = new LinkedHashMap<String,SortRule>();

    /*
     * Adds a TestRule with its name and around parameter to SortRules.
     * @param rule: the TestRule to be added to SortRules
     * @param name: the name of the TestRule
     * @param around: the annotated around parameter of the TestRule
     */
    public void add(TestRule rule, String name, String around) {
        if (around.equals(name)) {
            throw new RuntimeException("Rule '" + name + "' has itself as around value");
        }
        rules.put(name, new SortRule(rule, name, around));
    }

    /*
     * Returns the stored rules in insertion order.
     * @return A list of TestRules in the order of their insertion.
     */
    public List<TestRule> unsortedRules() {
        List<TestRule> result = new ArrayList<TestRule>();
        for (SortRule rule : rules.values()) result.add(rule.testRule);
        return result;
    }

    /*
     * Returns the stored TestRules in the order specified by their around parameters.
     * All TestRules annotated with or referenced by an around parameter are
     * returned inside one single RuleChain inside this list which ensures ordering.
     */
    public List<TestRule> sortedAndChainedRules() {
        List<TestRule>       sortedRules = new ArrayList<TestRule>(rules.size());
        Map<String,SortRule> rulesCopy   = new LinkedHashMap<String,SortRule>(rules);
        Set<String>          removeMe    = new HashSet<String>();
        List<String>         arounds     = new ArrayList<String>();
        for (SortRule rule : rulesCopy.values()) arounds.add(rule.around);

        for (SortRule rule : rulesCopy.values()) {
            int pointers = Collections.frequency(arounds, rule.name);
            if (pointers == 0 && rule.around.equals("")) {
                sortedRules.add(rule.testRule);
                removeMe.add(rule.name);
            } else {
                if (!rule.around.equals("") && !rulesCopy.containsKey(rule.around)) {
                    throw new RuntimeException("The around value '"
                                               + rule.around
                                               + "' doesn't specify a given TestRule.");
                }
                rule.pointAtMe = pointers;
            }
        }
        removeAll(rulesCopy,removeMe);

        RuleChain ruleChain = RuleChain.emptyRuleChain();
        while (!rulesCopy.isEmpty()) {
            boolean containsCycle = true;
            for (SortRule rule : rulesCopy.values()) {
                if (rule.pointAtMe == 0) {
                    containsCycle = false;
                    ruleChain     = ruleChain.around(rule.testRule);
                    rule.decrementPointAtMeOfMyAround(rulesCopy);
                    removeMe.add(rule.name);
                }
            }
            if (containsCycle) throw new RuntimeException("Rules are chained cyclic.");
            removeAll(rulesCopy,removeMe);
        }
        sortedRules.add(ruleChain);

        return sortedRules;
    }

    private void removeAll(Map<String,SortRule> rules, Set<String> removeMe) {
        for (String name : removeMe) rules.remove(name);
        removeMe.clear();
    }

    private class SortRule {
        private final TestRule testRule;
        private final String   name;
        private final String   around;
        private       int      pointAtMe;

        private SortRule(TestRule testRule, String name, String around) {
            this.testRule  = testRule;
            this.name      = name;
            this.around    = around;
            this.pointAtMe = 0;
        }

        private void decrementPointAtMeOfMyAround(Map<String,SortRule> rules) {
            if (!around.equals("") && rules.containsKey(around)) {
                rules.get(around).pointAtMe -= 1;
            }
        }
    }
}
