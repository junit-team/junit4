package org.junit.rules;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.manipulation.GlobalRuleRunner;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests to exercise global-level rules.
 */
public class GlobalRulesTest {
    private static int count = 0;

    public static class Counter implements TestRule {
        public Statement apply(Statement base, Description description) {
            count++;
            return base;
        }
    }

    public static class ExampleTest {
        @Test
        public void firstTest() {
            assertEquals(2, count);
        }

        @Test
        public void secondTest() {
            assertEquals(2, count);
        }
    }

    public static class SecondExampleTest {
        @Test
        public void firstTest() {
            assertEquals(3, count);
        }

        @Test
        public void secondTest() {
            assertEquals(3, count);
        }
    }

    @Test
    public void rulesAreAppliedOnEveryTest() {
        List<Class<?>> rules = new ArrayList<Class<?>>();
        rules.add(Counter.class);
        new JUnitCore().run(Request.classes(ExampleTest.class, SecondExampleTest.class)
                .withGlobalRules(new GlobalRuleRunner(rules)));
        assertEquals(3, count);
    }
}
