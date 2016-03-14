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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatchman;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

@SuppressWarnings("deprecation")
public class MethodRulesTest {
    private static boolean wasRun;

    public static class ExampleTest {
        @Rule
        public MethodRule example = new MethodRule() {
            public Statement apply(final Statement base,
                    FrameworkMethod method, Object target) {
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

    public static class SonOfExampleTest extends ExampleTest {

    }

    @Test
    public void ruleIsIntroducedAndEvaluatedOnSubclass() {
        wasRun = false;
        JUnitCore.runClasses(SonOfExampleTest.class);
        assertTrue(wasRun);
    }

    private static int runCount;

    private static class Increment implements MethodRule {
        public Statement apply(final Statement base,
                FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    runCount++;
                    base.evaluate();
                }
            };
        }
    }
    
    public static class MultipleRuleTest {

        @Rule
        public MethodRule incrementor1 = new Increment();

        @Rule
        public MethodRule incrementor2 = new Increment();

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
        public MethodRule watchman = new TestWatchman() {
            @Override
            public void failed(Throwable e, FrameworkMethod method) {
                log += method.getName() + " " + e.getClass().getSimpleName();
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
        assertEquals("nothing AssertionError", log);
        assertEquals(1, result.getFailureCount());
    }

    public static class WatchmanTest {
        private static String watchedLog;

        @Rule
        public MethodRule watchman = new TestWatchman() {
            @Override
            public void failed(Throwable e, FrameworkMethod method) {
                watchedLog += method.getName() + " "
                        + e.getClass().getSimpleName() + "\n";
            }

            @Override
            public void succeeded(FrameworkMethod method) {
                watchedLog += method.getName() + " " + "success!\n";
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
        assertThat(WatchmanTest.watchedLog, containsString("fails AssertionError"));
        assertThat(WatchmanTest.watchedLog, containsString("succeeds success!"));
    }

    public static class BeforesAndAfters {
        private static String watchedLog;

        @Before
        public void before() {
            watchedLog += "before ";
        }

        @Rule
        public MethodRule watchman = new TestWatchman() {
            @Override
            public void starting(FrameworkMethod method) {
                watchedLog += "starting ";
            }

            @Override
            public void finished(FrameworkMethod method) {
                watchedLog += "finished ";
            }

            @Override
            public void succeeded(FrameworkMethod method) {
                watchedLog += "succeeded ";
            }
        };

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
    public void beforesAndAfters() {
        BeforesAndAfters.watchedLog = "";
        JUnitCore.runClasses(BeforesAndAfters.class);
        assertThat(BeforesAndAfters.watchedLog, is("starting before test after succeeded finished "));
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
    
    public static class HasMethodReturningMethodRule {
        private MethodRule methodRule = new MethodRule() {
            public Statement apply(final Statement base, FrameworkMethod method, Object target) {
                return new Statement() {
                    
                    @Override
                    public void evaluate() throws Throwable {
                        wasRun = true;
                        base.evaluate();
                    }
                };
            }
        };
        
        @Rule
        public MethodRule methodRule() {
            return methodRule;
        }
        
        @Test
        public void doNothing() {
            
        }
    }

    /**
     * If there are any public methods annotated with @Rule returning a {@link MethodRule}
     * then it should also be run.
     * 
     * <p>This case has been added with 
     * <a href="https://github.com/junit-team/junit/issues/589">Issue #589</a> - 
     * Support @Rule for methods works only for TestRule but not for MethodRule
     */
    @Test
    public void runsMethodRuleThatIsReturnedByMethod() {
        wasRun = false;
        JUnitCore.runClasses(HasMethodReturningMethodRule.class);
        assertTrue(wasRun);
    }
    
    public static class HasMultipleMethodsReturningMethodRule {
        @Rule
        public Increment methodRule1() {
            return new Increment();
        }
        
        @Rule
        public Increment methodRule2() {
            return new Increment();
        }
        
        @Test
        public void doNothing() {
            
        }
    }

    /**
     * If there are multiple public methods annotated with @Rule returning a {@link MethodRule}
     * then all the rules returned should be run.
     * 
     * <p>This case has been added with 
     * <a href="https://github.com/junit-team/junit/issues/589">Issue #589</a> - 
     * Support @Rule for methods works only for TestRule but not for MethodRule
     */
    @Test
    public void runsAllMethodRulesThatAreReturnedByMethods() {
        runCount = 0;
        JUnitCore.runClasses(HasMultipleMethodsReturningMethodRule.class);
        assertEquals(2, runCount);
    }
    
    
    public static class CallsMethodReturningRuleOnlyOnce {
        int callCount = 0;
        
        private static class Dummy implements MethodRule {
            public Statement apply(final Statement base, FrameworkMethod method, Object target) {
                return new Statement() {
                    
                    @Override
                    public void evaluate() throws Throwable {
                        base.evaluate();
                    }
                };
            }
        };
       
        
        @Rule
        public MethodRule methodRule() {
            callCount++;
            return new Dummy();
        }
        
        @Test
        public void doNothing() {
            assertEquals(1, callCount);
        }
    }

    /**
     * If there are any public methods annotated with @Rule returning a {@link MethodRule}
     * then method should be called only once.
     * 
     * <p>This case has been added with 
     * <a href="https://github.com/junit-team/junit/issues/589">Issue #589</a> - 
     * Support @Rule for methods works only for TestRule but not for MethodRule
     */
    @Test
    public void callsMethodReturningRuleOnlyOnce() {
        assertTrue(JUnitCore.runClasses(CallsMethodReturningRuleOnlyOnce.class).wasSuccessful());
    }
}
