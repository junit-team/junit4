package org.junit.tests.experimental.rules;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class TestRuleTest {
    private static boolean wasRun;

    public static class ExampleTest {
        @Rule
        public TestRule example = new TestRule() {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        wasRun = true;
                        base.evaluate();
                    }

                    ;
                };
            }
        };

        @Test
        public void nothing() {

        }
    }

    @Test
    public void ruleIsIntroducedAndEvaluated() {
        wasRun = false;
        JUnitCore.runClasses(ExampleTest.class);
        assertTrue(wasRun);
    }

    public static class BothKindsOfRule implements TestRule, org.junit.rules.MethodRule {
        public int applications = 0;

        public Statement apply(Statement base, FrameworkMethod method,
                Object target) {
            applications++;
            return base;
        }

        public Statement apply(Statement base, Description description) {
            applications++;
            return base;
        }
    }

    public static class OneFieldTwoKindsOfRule {
        @Rule
        public BothKindsOfRule both = new BothKindsOfRule();

        @Test
        public void onlyOnce() {
            assertEquals(1, both.applications);
        }
    }


    @Test
    public void onlyApplyOnceEvenIfImplementsBothInterfaces() {
        assertTrue(JUnitCore.runClasses(OneFieldTwoKindsOfRule.class).wasSuccessful());
    }

    public static class SonOfExampleTest extends ExampleTest {

    }

    @Test
    public void ruleIsIntroducedAndEvaluatedOnSubclass() {
        wasRun = false;
        JUnitCore.runClasses(SonOfExampleTest.class);
        assertTrue(wasRun);
    }

    private static int runCount;

    public static class MultipleRuleTest {
        private static class Increment implements TestRule {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        runCount++;
                        base.evaluate();
                    }

                    ;
                };
            }
        }

        @Rule
        public TestRule incrementor1 = new Increment();

        @Rule
        public TestRule incrementor2 = new Increment();

        @Test
        public void nothing() {

        }
    }

    @Test
    public void multipleRulesAreRun() {
        runCount = 0;
        JUnitCore.runClasses(MultipleRuleTest.class);
        assertEquals(2, runCount);
    }

    public static class NoRulesTest {
        public int x;

        @Test
        public void nothing() {

        }
    }

    @Test
    public void ignoreNonRules() {
        Result result = JUnitCore.runClasses(NoRulesTest.class);
        assertEquals(0, result.getFailureCount());
    }

    private static String log;

    public static class OnFailureTest {
        @Rule
        public TestRule watcher = new TestWatcher() {
            @Override
            protected void failed(Throwable e, Description description) {
                log += description + " " + e.getClass().getSimpleName();
            }
        };

        @Test
        public void nothing() {
            fail();
        }
    }

    @Test
    public void onFailure() {
        log = "";
        Result result = JUnitCore.runClasses(OnFailureTest.class);
        assertEquals(String.format("nothing(%s) AssertionError", OnFailureTest.class.getName()), log);
        assertEquals(1, result.getFailureCount());
    }

    public static class WatchmanTest {
        private static String watchedLog;

        @Rule
        public TestRule watcher = new TestWatcher() {
            @Override
            protected void failed(Throwable e, Description description) {
                watchedLog += description + " "
                        + e.getClass().getSimpleName() + "\n";
            }

            @Override
            protected void succeeded(Description description) {
                watchedLog += description + " " + "success!\n";
            }
        };

        @Test
        public void fails() {
            fail();
        }

        @Test
        public void succeeds() {
        }
    }

    @Test
    public void succeeded() {
        WatchmanTest.watchedLog = "";
        JUnitCore.runClasses(WatchmanTest.class);
        assertThat(WatchmanTest.watchedLog, containsString(String.format("fails(%s) AssertionError", WatchmanTest.class.getName())));
        assertThat(WatchmanTest.watchedLog, containsString(String.format("succeeds(%s) success!", WatchmanTest.class.getName())));
    }

    public static class BeforesAndAfters {
        private static StringBuilder watchedLog = new StringBuilder();

        @Before
        public void before() {
            watchedLog.append("before ");
        }

        @Rule
        public TestRule watcher = new LoggingTestWatcher(watchedLog);

        @After
        public void after() {
            watchedLog.append("after ");
        }

        @Test
        public void succeeds() {
            watchedLog.append("test ");
        }
    }

    @Test
    public void beforesAndAfters() {
        BeforesAndAfters.watchedLog = new StringBuilder();
        JUnitCore.runClasses(BeforesAndAfters.class);
        assertThat(BeforesAndAfters.watchedLog.toString(),
                is("starting before test after succeeded finished "));
    }

    public static class WrongTypedField {
        @Rule
        public int x = 5;

        @Test
        public void foo() {
        }
    }

    @Test
    public void validateWrongTypedField() {
        assertThat(testResult(WrongTypedField.class),
                hasSingleFailureContaining("must implement MethodRule"));
    }

    public static class SonOfWrongTypedField extends WrongTypedField {

    }

    @Test
    public void validateWrongTypedFieldInSuperclass() {
        assertThat(testResult(SonOfWrongTypedField.class),
                hasSingleFailureContaining("must implement MethodRule"));
    }

    public static class PrivateRule {
        @Rule
        private TestRule rule = new TestName();

        @Test
        public void foo() {
        }
    }

    @Test
    public void validatePrivateRule() {
        assertThat(testResult(PrivateRule.class),
                hasSingleFailureContaining("must be public"));
    }

    public static class CustomTestName implements TestRule {
        public String name = null;

        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    name = description.getMethodName();
                    base.evaluate();
                }
            };
        }
    }

    public static class UsesCustomMethodRule {
        @Rule
        public CustomTestName counter = new CustomTestName();

        @Test
        public void foo() {
            assertEquals("foo", counter.name);
        }
    }

    @Test
    public void useCustomMethodRule() {
        assertThat(testResult(UsesCustomMethodRule.class), isSuccessful());
    }

    public static class MethodExampleTest {
        private TestRule example = new TestRule() {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        wasRun = true;
                        base.evaluate();
                    }

                    ;
                };
            }
        };

        @Rule
        public TestRule getExample() {
            return example;
        }

        @Test
        public void nothing() {

        }
    }

    @Test
    public void methodRuleIsIntroducedAndEvaluated() {
        wasRun = false;
        JUnitCore.runClasses(MethodExampleTest.class);
        assertTrue(wasRun);
    }

    public static class MethodBothKindsOfRule implements TestRule, org.junit.rules.MethodRule {
        public int applications = 0;

        public Statement apply(Statement base, FrameworkMethod method,
                Object target) {
            applications++;
            return base;
        }

        public Statement apply(Statement base, Description description) {
            applications++;
            return base;
        }
    }

    public static class MethodOneFieldTwoKindsOfRule {
        private MethodBothKindsOfRule both = new MethodBothKindsOfRule();

        @Rule
        public MethodBothKindsOfRule getBoth() {
            return both;
        }

        @Test
        public void onlyOnce() {
            assertEquals(1, both.applications);
        }
    }


    @Test
    public void methodOnlyApplyOnceEvenIfImplementsBothInterfaces() {
        assertTrue(JUnitCore.runClasses(MethodOneFieldTwoKindsOfRule.class).wasSuccessful());
    }

    public static class MethodSonOfExampleTest extends MethodExampleTest {

    }

    @Test
    public void methodRuleIsIntroducedAndEvaluatedOnSubclass() {
        wasRun = false;
        JUnitCore.runClasses(MethodSonOfExampleTest.class);
        assertTrue(wasRun);
    }

    public static class MethodMultipleRuleTest {
        private static class Increment implements TestRule {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        runCount++;
                        base.evaluate();
                    }

                    ;
                };
            }
        }

        private TestRule incrementor1 = new Increment();

        @Rule
        public TestRule getIncrementor1() {
            return incrementor1;
        }

        private TestRule incrementor2 = new Increment();

        @Rule
        public TestRule getIncrementor2() {
            return incrementor2;
        }

        @Test
        public void nothing() {

        }
    }

    @Test
    public void methodMultipleRulesAreRun() {
        runCount = 0;
        JUnitCore.runClasses(MethodMultipleRuleTest.class);
        assertEquals(2, runCount);
    }

    public static class MethodNoRulesTest {
        public int x;

        @Test
        public void nothing() {

        }
    }

    @Test
    public void methodIgnoreNonRules() {
        Result result = JUnitCore.runClasses(MethodNoRulesTest.class);
        assertEquals(0, result.getFailureCount());
    }

    public static class MethodOnFailureTest {
        private TestRule watchman = new TestWatcher() {
            @Override
            protected void failed(Throwable e, Description description) {
                log += description + " " + e.getClass().getSimpleName();
            }
        };

        @Rule
        public TestRule getWatchman() {
            return watchman;
        }

        @Test
        public void nothing() {
            fail();
        }
    }

    @Test
    public void methodOnFailure() {
        log = "";
        Result result = JUnitCore.runClasses(MethodOnFailureTest.class);
        assertEquals(String.format("nothing(%s) AssertionError", MethodOnFailureTest.class.getName()), log);
        assertEquals(1, result.getFailureCount());
    }

    public static class MethodOnSkippedTest {
        private TestRule watchman = new TestWatcher() {
            @Override
            protected void skipped(AssumptionViolatedException e, Description description) {
                log += description + " " + e.getClass().getSimpleName();
            }
        };

        @Rule
        public TestRule getWatchman() {
            return watchman;
        }

        @Test
        public void nothing() {
            Assume.assumeTrue(false);
        }
    }

    @Test
    public void methodOnSkipped() {
        log = "";
        Result result = JUnitCore.runClasses(MethodOnSkippedTest.class);
        assertEquals(String.format("nothing(%s) AssumptionViolatedException", MethodOnSkippedTest.class.getName()), log);
        assertEquals(0, result.getFailureCount());
        assertEquals(1, result.getRunCount());
    }

    public static class MethodWatchmanTest {
        @SuppressWarnings("unused")
        private static String watchedLog;

        private TestRule watchman = new TestWatcher() {
            @Override
            protected void failed(Throwable e, Description description) {
                watchedLog += description + " "
                        + e.getClass().getSimpleName() + "\n";
            }

            @Override
            protected void succeeded(Description description) {
                watchedLog += description + " " + "success!\n";
            }
        };

        @Rule
        public TestRule getWatchman() {
            return watchman;
        }

        @Test
        public void fails() {
            fail();
        }

        @Test
        public void succeeds() {
        }
    }

    @Test
    public void methodSucceeded() {
        WatchmanTest.watchedLog = "";
        JUnitCore.runClasses(WatchmanTest.class);
        assertThat(WatchmanTest.watchedLog, containsString(String.format("fails(%s) AssertionError", WatchmanTest.class.getName())));
        assertThat(WatchmanTest.watchedLog, containsString(String.format("succeeds(%s) success!", WatchmanTest.class.getName())));
    }

    public static class MethodBeforesAndAfters {
        private static String watchedLog;

        @Before
        public void before() {
            watchedLog += "before ";
        }

        private TestRule watchman = new TestWatcher() {
            @Override
            protected void starting(Description d) {
                watchedLog += "starting ";
            }

            @Override
            protected void finished(Description d) {
                watchedLog += "finished ";
            }

            @Override
            protected void succeeded(Description d) {
                watchedLog += "succeeded ";
            }
        };

        @Rule
        public TestRule getWatchman() {
            return watchman;
        }

        @After
        public void after() {
            watchedLog += "after ";
        }

        @Test
        public void succeeds() {
            watchedLog += "test ";
        }
    }

    @Test
    public void methodBeforesAndAfters() {
        MethodBeforesAndAfters.watchedLog = "";
        JUnitCore.runClasses(MethodBeforesAndAfters.class);
        assertThat(MethodBeforesAndAfters.watchedLog, is("starting before test after succeeded finished "));
    }

    public static class MethodWrongTypedField {
        @Rule
        public int getX() {
            return 5;
        }

        @Test
        public void foo() {
        }
    }

    @Test
    public void methodValidateWrongTypedField() {
        assertThat(testResult(MethodWrongTypedField.class),
                hasSingleFailureContaining("must return an implementation of MethodRule"));
    }

    public static class MethodSonOfWrongTypedField extends MethodWrongTypedField {

    }

    @Test
    public void methodValidateWrongTypedFieldInSuperclass() {
        assertThat(testResult(MethodSonOfWrongTypedField.class),
                hasSingleFailureContaining("must return an implementation of MethodRule"));
    }

    public static class MethodPrivateRule {
        @Rule
        private TestRule getRule() {
            return new TestName();
        }

        @Test
        public void foo() {
        }
    }

    @Test
    public void methodValidatePrivateRule() {
        assertThat(testResult(MethodPrivateRule.class),
                hasSingleFailureContaining("must be public"));
    }

    public static class MethodUsesCustomMethodRule {
        private CustomTestName counter = new CustomTestName();

        @Rule
        public CustomTestName getCounter() {
            return counter;
        }

        @Test
        public void foo() {
            assertEquals("foo", counter.name);
        }
    }

    @Test
    public void methodUseCustomMethodRule() {
        assertThat(testResult(MethodUsesCustomMethodRule.class), isSuccessful());
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
        @Rule
        public OrderTestRule orderMethod() {
            return new OrderTestRule("orderMethod");
        }

        @Rule
        public OrderTestRule orderField = new OrderTestRule("orderField");

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

    public static class CallMethodOnlyOnceRule {
        int countOfMethodCalls = 0;

        private static class Dummy implements TestRule {
            public Statement apply(final Statement base, Description description) {
                return new Statement() {
                    @Override
                    public void evaluate() throws Throwable {
                        base.evaluate();
                    }

                    ;
                };
            }
        }

        @Rule
        public Dummy both() {
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
        assertTrue(JUnitCore.runClasses(CallMethodOnlyOnceRule.class).wasSuccessful());
    }
}
