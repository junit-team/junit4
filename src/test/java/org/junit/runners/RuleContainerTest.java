package org.junit.runners;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class RuleContainerTest {
    private final RuleContainer container = new RuleContainer();

    @Test
    public void methodRulesOnly() {
        container.add(MRule.M1);
        container.add(MRule.M2);
        assertEquals("[M1, M2]", container.getSortedRules().toString());
        container.setOrder(MRule.M2, 1);
        assertEquals("[M2, M1]", container.getSortedRules().toString());
    }

    @Test
    public void testRuleAroundMethodRule() {
        container.add(MRule.M1);
        container.add(Rule.A);
        assertEquals("[M1, A]", container.getSortedRules().toString());
    }

    @Test
    public void ordering1() {
        container.add(MRule.M1);
        container.add(Rule.A);
        container.setOrder(Rule.A, 1);
        assertEquals("[A, M1]", container.getSortedRules().toString());
    }

    @Test
    public void ordering2() {
        container.add(Rule.A);
        container.add(Rule.B);
        container.add(Rule.C);
        assertEquals("[A, B, C]", container.getSortedRules().toString());
        container.setOrder(Rule.B, 1);
        container.setOrder(Rule.C, 2);
        assertEquals("[C, B, A]", container.getSortedRules().toString());
    }

    private enum Rule implements TestRule {
        A,
        B,
        C;

        public Statement apply(Statement base, Description description) {
            return base;
        }
    }

    private enum MRule implements MethodRule {
        M1,
        M2;

        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            return base;
        }
    }
}
