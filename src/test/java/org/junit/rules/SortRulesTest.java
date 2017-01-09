package org.junit.rules;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.junit.experimental.results.PrintableResult.testResult;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class SortRulesTest {
    private static final List<String> LOG = new ArrayList<String>();

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

    @After
    public void tearDown() {
        LOG.clear();
    }

    private String getExceptionMessage(Result result) {
        return result.getFailures().get(0).getException().getMessage();
    }

    public static class RuleHasNonExistentAroundValue {
        @Rule(around="foo")
        public final TestRule rule = RuleChain.emptyRuleChain();

        @Test
        public void example() {}
    }

    @Test
    public void throwExceptionWhenRuleHasNonExistentAroundValue() {
        Result result = new JUnitCore().run(RuleHasNonExistentAroundValue.class);

        assertThat(result.getFailures().size(), equalTo(1));
        assertThat(getExceptionMessage(result),
                   equalTo("The around value 'foo' doesn't specify a given TestRule."));
    }

    public static class RuleHasSelfAsAroundValue {
        @Rule(around="test")
        public final TestRule test = RuleChain.emptyRuleChain();

        @Test
        public void example() {}
    }

    @Test
    public void throwExceptionWhenRuleHasSelfAsAroundValue() {
        Result result = new JUnitCore().run(RuleHasSelfAsAroundValue.class);

        assertThat(result.getFailures().size(), equalTo(1));
        assertThat(getExceptionMessage(result),
                   equalTo("Rule 'test' has itself as around value"));
    }

    public static class RulesAreChainedCyclic {
        //Cycle
        @Rule(around="ruleB")
        public final TestRule ruleA = RuleChain.emptyRuleChain();

        @Rule(around="ruleC")
        public final TestRule ruleB = RuleChain.emptyRuleChain();

        @Rule(around="ruleD")
        public final TestRule ruleC = RuleChain.emptyRuleChain();

        @Rule(around="ruleE")
        public final TestRule ruleD = RuleChain.emptyRuleChain();

        @Rule(around="ruleA")
        public final TestRule ruleE = RuleChain.emptyRuleChain();

        //RuleChain pointing to member of cycle
        @Rule(around="ruleY")
        public final TestRule ruleZ = RuleChain.emptyRuleChain();

        @Rule(around="ruleX")
        public final TestRule ruleY = RuleChain.emptyRuleChain();

        @Rule(around="rulePointingToCycle")
        public final TestRule ruleX = RuleChain.emptyRuleChain();

        @Rule(around="ruleC")
        public final TestRule rulePointingToCycle = RuleChain.emptyRuleChain();

        @Test
        public void example() {}
    }

    @Test
    public void throwExceptionWhenRulesAreChainedCyclic() {
        Result result = new JUnitCore().run(RulesAreChainedCyclic.class);

        assertThat(result.getFailures().size(), equalTo(1));
        assertThat(getExceptionMessage(result),
                   equalTo("Rules are chained cyclic."));
    }

    public static class ExampleMethodRule implements MethodRule {

        public Statement apply(Statement base, FrameworkMethod method,
                Object target) {
            return base;
        }
    }

    public static class RuleAnnotatedToBeChainedAroundMethodRule {
        @Rule
        public final MethodRule methodRule = new ExampleMethodRule();

        @Rule(around="methodRule")
        public final TestRule rule = RuleChain.emptyRuleChain();

        @Test
        public void example() {}
    }

    @Test
    public void throwExceptionWhenRuleIsToBeChainedAroundMethodRule() {
        Result result = new JUnitCore().run(RuleAnnotatedToBeChainedAroundMethodRule.class);

        assertThat(result.getFailures().size(), equalTo(1));
        assertThat(getExceptionMessage(result),
                   equalTo("The around value 'methodRule' doesn't specify a given TestRule."));
    }

    public static class UseSingleRuleChainConstructedByAnnotation {
        @Rule(around="outerRule")
        public final TestRule outerOuterRule = new LoggingRule("outerOuter rule");

        @Rule(around="middleRule")
        public final TestRule outerRule = new LoggingRule("outer rule");

        @Rule(around="innerRule")
        public final TestRule middleRule = new LoggingRule("middle rule");

        @Rule(around="innerInnerRule")
        public final TestRule innerRule = new LoggingRule("inner rule");

        @Rule
        public final TestRule innerInnerRule = new LoggingRule("innerInner rule");

        @Test
        public void example() {}
    }

    @Test
    public void executeRulesChainedByAnnotationInCorrectOrder() {
        testResult(UseSingleRuleChainConstructedByAnnotation.class);
        List<String> expectedLog = asList("starting outerOuter rule", "starting outer rule",
                "starting middle rule", "starting inner rule", "starting innerInner rule",
                "finished innerInner rule", "finished inner rule", "finished middle rule",
                "finished outer rule", "finished outerOuter rule");
        assertEquals(expectedLog, LOG);
    }

    public static class UseSingleRuleChainConstructedByAnnotationAmbiguously {
        @Rule(around="middleRule123")
        public final TestRule outerRule1 = new LoggingRule("outer rule");

        @Rule(around="middleRule123")
        public final TestRule outerRule2 = new LoggingRule("outer rule");

        @Rule(around="middleRule123")
        public final TestRule outerRule3 = new LoggingRule("outer rule");

        @Rule(around="innerRule123")
        public final TestRule middleRule123 = new LoggingRule("middle rule");

        @Rule
        public final TestRule innerRule123 = new LoggingRule("inner rule");

        @Test
        public void example() {}
    }

    @Test
    public void executeRulesChainedByAnnotationAmbiguouslyInCorrectOrder() {
        testResult(UseSingleRuleChainConstructedByAnnotationAmbiguously.class);
        List<String> expectedLog = asList("starting outer rule",
                "starting outer rule", "starting outer rule", "starting middle rule",
                "starting inner rule", "finished inner rule", "finished middle rule",
                "finished outer rule", "finished outer rule", "finished outer rule");
        assertTrue(LOG.equals(expectedLog));
    }

    public static class UseTwoIndependentRuleChainsConstructedByAnnotation {
        @Rule(around="middleRuleA")
        public final TestRule outerRuleA = new LoggingRule("outer rule A");

        @Rule(around="innerRuleA")
        public final TestRule middleRuleA = new LoggingRule("middle rule A");

        @Rule
        public final TestRule innerRuleA = new LoggingRule("inner rule A");

        @Rule(around="middleRuleB")
        public final TestRule outerRuleB = new LoggingRule("outer rule B");

        @Rule(around="innerRuleB")
        public final TestRule middleRuleB = new LoggingRule("middle rule B");

        @Rule
        public final TestRule innerRuleB = new LoggingRule("inner rule B");

        @Test
        public void example() {}
    }

    @Test
    public void executeRulesChainedByAnnotationInIndependentRuleChains() {
        testResult(UseTwoIndependentRuleChainsConstructedByAnnotation.class);
        String startingOuterA  = "starting outer rule A";
        String startingMiddleA = "starting middle rule A";
        String startingInnerA  = "starting inner rule A";
        String finishedInnerA  = "finished inner rule A";
        String finishedMiddleA = "finished middle rule A";
        String finishedOuterA  = "finished outer rule A";
        String startingOuterB  = "starting outer rule B";
        String startingMiddleB = "starting middle rule B";
        String startingInnerB  = "starting inner rule B";
        String finishedInnerB  = "finished inner rule B";
        String finishedMiddleB = "finished middle rule B";
        String finishedOuterB  = "finished outer rule B";

        assertEquals(12,LOG.size());
        assertTrue(LOG.indexOf(startingOuterA) < LOG.indexOf(startingMiddleA));
        assertTrue(LOG.indexOf(startingMiddleA) < LOG.indexOf(startingInnerA));
        assertTrue(LOG.indexOf(finishedInnerA) < LOG.indexOf(finishedMiddleA));
        assertTrue(LOG.indexOf(finishedMiddleA) < LOG.indexOf(finishedOuterA));
        assertTrue(LOG.indexOf(startingOuterB) < LOG.indexOf(startingMiddleB));
        assertTrue(LOG.indexOf(startingMiddleB) < LOG.indexOf(startingInnerB));
        assertTrue(LOG.indexOf(finishedInnerB) < LOG.indexOf(finishedMiddleB));
        assertTrue(LOG.indexOf(finishedMiddleB) < LOG.indexOf(finishedOuterB));
    }

    public static class UseAllValidRuleChainsCombined {
        //simple case
        @Rule(around="outerRule")
        public final TestRule outerOuterRule = new LoggingRule("outerOuter rule");

        @Rule(around="middleRule")
        public final TestRule outerRule = new LoggingRule("outer rule");

        @Rule(around="innerRule")
        public final TestRule middleRule = new LoggingRule("middle rule");

        @Rule(around="innerInnerRule")
        public final TestRule innerRule = new LoggingRule("inner rule");

        @Rule
        public final TestRule innerInnerRule = new LoggingRule("innerInner rule");

        //constructed ambiguously (two rules have the same around value)
        @Rule(around="middleRule123")
        public final TestRule outerRule1 = new LoggingRule("outer rule 123");

        @Rule(around="middleRule123")
        public final TestRule outerRule2 = new LoggingRule("outer rule 123");

        @Rule(around="middleRule123")
        public final TestRule outerRule3 = new LoggingRule("outer rule 123");

        @Rule(around="innerRule123")
        public final TestRule middleRule123 = new LoggingRule("middle rule 123");

        @Rule
        public final TestRule innerRule123 = new LoggingRule("inner rule 123");

        //two independent RuleChains
        @Rule(around="middleRuleA")
        public final TestRule outerRuleA = new LoggingRule("outer rule A");

        @Rule(around="innerRuleA")
        public final TestRule middleRuleA = new LoggingRule("middle rule A");

        @Rule
        public final TestRule innerRuleA = new LoggingRule("inner rule A");

        @Rule(around="middleRuleB")
        public final TestRule outerRuleB = new LoggingRule("outer rule B");

        @Rule(around="innerRuleB")
        public final TestRule middleRuleB = new LoggingRule("middle rule B");

        @Rule
        public final TestRule innerRuleB = new LoggingRule("inner rule B");

        @Test
        public void example() {}
    }

    @Test
    public void executeComplexlyCombinedRulesInCorrectOrder() {
        testResult(UseAllValidRuleChainsCombined.class);

        String startingOuterOuter = "starting outerOuter rule";
        String startingOuter      = "starting outer rule";
        String startingMiddle     = "starting middle rule";
        String startingInner      = "starting inner rule";
        String startingInnerInner = "starting innerInner rule";
        String finishedOuterOuter = "finished outerOuter rule";
        String finishedOuter      = "finished outer rule";
        String finishedMiddle     = "finished middle rule";
        String finishedInner      = "finished inner rule";
        String finishedInnerInner = "finished innerInner rule";

        String startingOuter123 = "starting outer rule 123";
        String startingMiddle123 = "starting middle rule 123";
        String startingInner123 = "starting inner rule 123";
        String finishedInner123 = "finished inner rule 123";
        String finishedMiddle123 = "finished middle rule 123";
        String finishedOuter123 = "finished outer rule 123";

        String startingOuterA  = "starting outer rule A";
        String startingMiddleA = "starting middle rule A";
        String startingInnerA  = "starting inner rule A";
        String finishedInnerA  = "finished inner rule A";
        String finishedMiddleA = "finished middle rule A";
        String finishedOuterA  = "finished outer rule A";
        String startingOuterB  = "starting outer rule B";
        String startingMiddleB = "starting middle rule B";
        String startingInnerB  = "starting inner rule B";
        String finishedInnerB  = "finished inner rule B";
        String finishedMiddleB = "finished middle rule B";
        String finishedOuterB  = "finished outer rule B";

        assertEquals(32,LOG.size());

        assertTrue(LOG.indexOf(startingOuterOuter) < LOG.indexOf(startingOuter));
        assertTrue(LOG.indexOf(startingOuter) < LOG.indexOf(startingMiddle));
        assertTrue(LOG.indexOf(startingMiddle) < LOG.indexOf(startingInner));
        assertTrue(LOG.indexOf(startingInner) < LOG.indexOf(startingInnerInner));
        assertTrue(LOG.indexOf(finishedInnerInner) < LOG.indexOf(finishedInner));
        assertTrue(LOG.indexOf(finishedInner) < LOG.indexOf(finishedMiddle));
        assertTrue(LOG.indexOf(finishedMiddle) < LOG.indexOf(finishedOuter));
        assertTrue(LOG.indexOf(finishedOuter) < LOG.indexOf(finishedOuterOuter));

        assertTrue(LOG.indexOf(startingOuter123) < LOG.indexOf(startingMiddle123));
        assertTrue(LOG.indexOf(startingMiddle123) < LOG.indexOf(startingInner123));
        assertTrue(LOG.indexOf(finishedInner123) < LOG.indexOf(finishedMiddle123));
        assertTrue(LOG.indexOf(finishedMiddle123) < LOG.indexOf(finishedOuter123));

        assertTrue(LOG.indexOf(startingOuterA) < LOG.indexOf(startingMiddleA));
        assertTrue(LOG.indexOf(startingMiddleA) < LOG.indexOf(startingInnerA));
        assertTrue(LOG.indexOf(finishedInnerA) < LOG.indexOf(finishedMiddleA));
        assertTrue(LOG.indexOf(finishedMiddleA) < LOG.indexOf(finishedOuterA));
        assertTrue(LOG.indexOf(startingOuterB) < LOG.indexOf(startingMiddleB));
        assertTrue(LOG.indexOf(startingMiddleB) < LOG.indexOf(startingInnerB));
        assertTrue(LOG.indexOf(finishedInnerB) < LOG.indexOf(finishedMiddleB));
        assertTrue(LOG.indexOf(finishedMiddleB) < LOG.indexOf(finishedOuterB));
    }
}
