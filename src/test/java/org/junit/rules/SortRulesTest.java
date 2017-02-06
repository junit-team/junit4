package org.junit.rules;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.junit.experimental.results.PrintableResult.testResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.junit.Rule;

public class SortRulesTest {

    private static final List<String> LOG = new ArrayList<String>();

    @After
    public void tearDown() {
        LOG.clear();
    }

    private static class LoggingRule extends TestWatcher {
        private final String label;

        public LoggingRule(String label) {
            this.label = label;
        }

        @Override
        protected void starting(Description description) {
            LOG.add("starting " + label);
        }

        @Override
        protected void finished(Description description) {
            LOG.add("finished " + label);
        }
    }

    public static class IdentityRule implements TestRule {
        public Statement apply(final Statement base, final Description description) {
          return base;
        }
      }

    @Test
    public final void testSortListWithoutPriorities() {
        SortRules testObject = new SortRules();
        testObject.add(new IdentityRule(), -1);
        List<TestRule> result = testObject.listWithAutoconstructedRuleChain();
        //only the IdentityRule is inserted and not an empty RuleChain.
        assertEquals(result.size(),1);
        assertTrue(result.get(0) instanceof IdentityRule);
    }

    @Test
    public final void testSortList() {
        SortRules testObject = new SortRules();
        testObject.add(new LoggingRule(""), 0);
        testObject.add(new LoggingRule(""), 0);
        testObject.add(new IdentityRule(), -2);
        testObject.add(new IdentityRule(), -1);
        List<TestRule> result = testObject.listWithAutoconstructedRuleChain();
        //all LoggingRules are cained into a RuleChain
        assertEquals(result.size(),3);
        assertTrue(result.get(0) instanceof IdentityRule);
        assertTrue(result.get(1) instanceof IdentityRule);
        assertTrue(result.get(2) instanceof RuleChain);
    }

    public static class SortRulesIntoRuleChain {
        @Rule(priority = 2)
        public final TestRule rule2 = new LoggingRule("rule2");

        @Rule(priority = 1)
        public final TestRule rule1 = new LoggingRule("rule1");

        @Rule
        public final TestRule rule = new LoggingRule("withoutPriority");

        @Rule(priority = 0)
        public final TestRule rule0 = new LoggingRule("rule0");

        @Rule(priority = 3)
        public final TestRule rule3 = new LoggingRule("rule3");

        @Test
        public void example() {}
    }

    @Test
    public void testSortRulesIntoRuleChain() {
        testResult(SortRulesIntoRuleChain.class);
        List<String> expectedLog = asList(
                "starting rule3","starting rule2", "starting rule1",
                "starting rule0","starting withoutPriority",
                "finished withoutPriority","finished rule0",
                "finished rule1", "finished rule2","finished rule3"
        );
        assertEquals(expectedLog, LOG);
    }

    public static class SortRulesWithIdenticalPriorities {
        @Rule(priority = 1)
        public final TestRule rule11 = new LoggingRule("rule1");

        @Rule(priority = 0)
        public final TestRule rule0 = new LoggingRule("rule0");

        @Rule(priority = 2)
        public final TestRule rule2 = new LoggingRule("rule2");

        @Rule(priority = 1)
        public final TestRule rule12 = new LoggingRule("rule1");

        @Test
        public void example() {}
    }

    @Test
    public void testSortRulesWithIdenticalPriorities() {
        testResult(SortRulesWithIdenticalPriorities.class);
        List<String> expectedLog = asList("starting rule2",
                "starting rule1", "starting rule1", "starting rule0",
                "finished rule0", "finished rule1", "finished rule1",
                "finished rule2");
        assertEquals(expectedLog, LOG);
    }
}
