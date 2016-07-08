package org.junit.rules;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class TestRuleAdapterTest {
    private static final List<String> LOG = new ArrayList<String>();

    class MyMethodRule implements MethodRule {
        public Statement apply(final Statement base,
                               FrameworkMethod method,
                               Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    base.evaluate();
                }
            };
        }
    }

    private static class LoggingRule implements MethodRule {
        private String label;

        LoggingRule(String label) {
            this.label = label;
        }

        public Statement apply(final Statement base, FrameworkMethod method,
                               Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    LOG.add("ran " + label);
                    base.evaluate();
                };
            };
        }
    };

    public static class UseRuleChain {
        @Rule
        public final RuleChain chain = RuleChain
                .outerRule(new TestRuleAdapter(new LoggingRule("outer rule")))
                .around(new TestRuleAdapter(new LoggingRule("middle rule")))
                .around(new TestRuleAdapter(new LoggingRule("inner rule")));

        @Test
        public void example() {
            assertTrue(true);
        }
    }

    @Test
    public void executeRulesInCorrectOrder() throws Exception {
        testResult(UseRuleChain.class);
        List<String> expectedLog = asList("ran outer rule",
                "ran middle rule", "ran inner rule");
        assertEquals(expectedLog, LOG);
    }
}
