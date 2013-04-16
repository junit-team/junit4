package org.junit.tests.experimental.rules;

import org.junit.Test;
import org.junit.internal.builders.ParameterRunnerBuilder;
import org.junit.rules.ParameterRule;
import org.junit.rules.ParameterRuleChain;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;

public class ParameterRuleChainTest {
    private static final List<String> LOG = new ArrayList<String>();

    private static class LoggingRule implements ParameterRule {
        private final String message;

        public LoggingRule(String message) {
            this.message = message;
        }

        public ParameterRunnerBuilder apply(ParameterRunnerBuilder builder) {
            LOG.add(message);
            return builder;
        }
    }

    @RunWith(Parameterized.class)
    public static class UseParameterRuleChain {

        @Parameterized.Parameter(0) public int param1;

        @Parameterized.UseParameterRule
        public static final org.junit.rules.ParameterRuleChain chain = ParameterRuleChain.outerRule(new LoggingRule("outer rule"))
                .around(new LoggingRule("middle rule")).around(
                        new LoggingRule("inner rule"));

        @Parameterized.Parameters
        public static Collection<Object[]> parameters() {
            return Arrays.asList(new Object[][]{new Object[]{1}});
        }

        @Test
        public void example() {
            assertTrue(true);
        }
    }

    @Test
    public void executeRulesInCorrectOrder() throws Exception {
        testResult(UseParameterRuleChain.class).toString();
        List<String> expectedLog = asList("inner rule", "middle rule",
                "outer rule");
        assertEquals(expectedLog, LOG);
    }
}