/*
 * Created Oct 19, 2009
 */
package org.junit.rules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.LinkedList;
import java.util.List;

import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.MethodSorters;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Tests to exercise class-level rules.
 */
public class ClassRulesTest {
    public static class Counter extends ExternalResource {
        public int count = 0;

        @Override
        protected void before() throws Throwable {
            count++;
        }
    }

    public static class ExampleTestWithClassRule {
        @ClassRule
        public static Counter counter = new Counter();

        @Test
        public void firstTest() {
            assertEquals(1, counter.count);
        }

        @Test
        public void secondTest() {
            assertEquals(1, counter.count);
        }
    }

    @Test
    public void ruleIsAppliedOnce() {
        ExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(ExampleTestWithClassRule.class);
        assertEquals(1, ExampleTestWithClassRule.counter.count);
    }

    public static class SubclassOfTestWithClassRule extends
            ExampleTestWithClassRule {

    }

    @Test
    public void ruleIsIntroducedAndEvaluatedOnSubclass() {
        ExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(SubclassOfTestWithClassRule.class);
        assertEquals(1, ExampleTestWithClassRule.counter.count);
    }

    public static class CustomCounter implements TestRule {
        public int count = 0;

        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    count++;
                    base.evaluate();
                }
            };
        }
    }

    public static class ExampleTestWithCustomClassRule {
        @ClassRule
        public static CustomCounter counter = new CustomCounter();

        @Test
        public void firstTest() {
            assertEquals(1, counter.count);
        }

        @Test
        public void secondTest() {
            assertEquals(1, counter.count);
        }
    }


    @Test
    public void customRuleIsAppliedOnce() {
        ExampleTestWithCustomClassRule.counter.count = 0;
        Result result = JUnitCore.runClasses(ExampleTestWithCustomClassRule.class);
        assertTrue(result.wasSuccessful());
        assertEquals(1, ExampleTestWithCustomClassRule.counter.count);
    }

    private static final List<String> orderList = new LinkedList<String>();

    private static class OrderTestRule implements TestRule {
        private String name;

        public OrderTestRule(String name) {
            this.name = name;
        }

        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    orderList.add(name);
                    base.evaluate();
                }
            };
        }
    }

    public static class UsesFieldAndMethodRule {
        @ClassRule
        public static OrderTestRule orderMethod() {
            return new OrderTestRule("orderMethod");
        }

        @ClassRule
        public static OrderTestRule orderField = new OrderTestRule("orderField");

        @Test
        public void foo() {
            assertEquals("orderField", orderList.get(0));
            assertEquals("orderMethod", orderList.get(1));
        }
    }

    @Test
    public void usesFieldAndMethodRule() {
        orderList.clear();
        assertThat(testResult(UsesFieldAndMethodRule.class), isSuccessful());
    }


    public static class MethodExampleTestWithClassRule {
        private static Counter counter = new Counter();

        @ClassRule
        public static Counter getCounter() {
            return counter;
        }

        @Test
        public void firstTest() {
            assertEquals(1, counter.count);
        }

        @Test
        public void secondTest() {
            assertEquals(1, counter.count);
        }
    }

    @Test
    public void methodRuleIsAppliedOnce() {
        MethodExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(MethodExampleTestWithClassRule.class);
        assertEquals(1, MethodExampleTestWithClassRule.counter.count);
    }

    public static class MethodSubclassOfTestWithClassRule extends
            MethodExampleTestWithClassRule {

    }

    @Test
    public void methodRuleIsIntroducedAndEvaluatedOnSubclass() {
        MethodExampleTestWithClassRule.counter.count = 0;
        JUnitCore.runClasses(MethodSubclassOfTestWithClassRule.class);
        assertEquals(1, MethodExampleTestWithClassRule.counter.count);
    }

    public static class MethodExampleTestWithCustomClassRule {
        private static CustomCounter counter = new CustomCounter();

        @ClassRule
        public static CustomCounter getCounter() {
            return counter;
        }

        @Test
        public void firstTest() {
            assertEquals(1, counter.count);
        }

        @Test
        public void secondTest() {
            assertEquals(1, counter.count);
        }
    }


    @Test
    public void methodCustomRuleIsAppliedOnce() {
        MethodExampleTestWithCustomClassRule.counter.count = 0;
        Result result = JUnitCore.runClasses(MethodExampleTestWithCustomClassRule.class);
        assertTrue(result.wasSuccessful());
        assertEquals(1, MethodExampleTestWithCustomClassRule.counter.count);
    }

    public static class CallMethodOnlyOnceRule {
        static int countOfMethodCalls = 0;

        private static class Dummy implements TestRule {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        base.evaluate();
                    }
                };
            }
        }

        @ClassRule
        public static Dummy both() {
            countOfMethodCalls++;
            return new Dummy();
        }

        @Test
        public void onlyOnce() {
            assertEquals(1, countOfMethodCalls);
        }
    }

    @Test
    public void testCallMethodOnlyOnceRule() {
        CallMethodOnlyOnceRule.countOfMethodCalls = 0;
        assertTrue(JUnitCore.runClasses(CallMethodOnlyOnceRule.class).wasSuccessful());
    }

    private static final StringBuilder log = new StringBuilder();

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class ClassRuleOrdering {
        @ClassRule(order = 1)
        public static TestRule a() {
            return new LoggingTestRule(log, "outer");
        }

        @ClassRule(order = 2)
        public static TestRule z() {
            return new LoggingTestRule(log, "inner");
        }

        @Test
        public void foo() {
            log.append(" foo");
        }

        @Test
        public void bar() {
            log.append(" bar");
        }
    }

    @Test
    public void classRuleOrdering() {
        log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRuleOrdering.class);
        assertTrue(result.wasSuccessful());
        assertEquals(" outer.begin inner.begin bar foo inner.end outer.end", log.toString());
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class ClassRuleOrderingDefault {
        @ClassRule
        public static TestRule a() {
            return new LoggingTestRule(log, "outer");
        }

        @ClassRule
        public static TestRule b() {
            return new LoggingTestRule(log, "inner");
        }

        @Test
        public void foo() {
            log.append(" foo");
        }

        @Test
        public void bar() {
            log.append(" bar");
        }
    }

    @Test
    public void classRuleOrderingDefault() {
        log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRuleOrderingDefault.class);
        assertTrue(result.wasSuccessful());
        assertEquals(" inner.begin outer.begin bar foo outer.end inner.end", log.toString());
    }

    public static class RunnerWithClassRuleAddedProgrammatically extends BlockJUnit4ClassRunner {
        public RunnerWithClassRuleAddedProgrammatically(Class testClass) throws InitializationError {
            super(testClass);
        }

        @Override
        protected List<TestRule> classRules() {
            final List<TestRule> rules = super.classRules();
            rules.add(new LoggingTestRule(log, "fromCode"));
            return rules;
        }
    }

    @RunWith(RunnerWithClassRuleAddedProgrammatically.class)
    public static class ClassRulesModifiableListEmpty {
        @Test
        public void test() {
            log.append(" test");
        }
    }

    @Test
    public void classRulesModifiableListEmpty() {
        log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRulesModifiableListEmpty.class);
        assertTrue(result.wasSuccessful());
        assertEquals(" fromCode.begin test fromCode.end", log.toString());
    }

    @RunWith(RunnerWithClassRuleAddedProgrammatically.class)
    public static class ClassRulesModifiableList {
        @ClassRule
        public static TestRule classRule() {
            return new LoggingTestRule(log, "classRule");
        }

        @Test
        public void test() {
            log.append(" test");
        }
    }

    @Test
    public void classRulesModifiableList() {
        log.setLength(0);
        Result result = JUnitCore.runClasses(ClassRulesModifiableList.class);
        assertTrue(result.wasSuccessful());
        assertEquals(" fromCode.begin classRule.begin test classRule.end fromCode.end", log.toString());
    }
}
