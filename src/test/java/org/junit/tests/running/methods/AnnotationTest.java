package org.junit.tests.running.methods;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class AnnotationTest extends TestCase {
    static boolean run;

    @Override
    public void setUp() {
        run = false;
    }

    static public class SimpleTest {
        @Test
        public void success() {
            run = true;
        }
    }

    public void testAnnotatedMethod() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(SimpleTest.class);
        assertTrue(run);
    }

    @RunWith(JUnit4.class)
    static public class SimpleTestWithFutureProofExplicitRunner {
        @Test
        public void success() {
            run = true;
        }
    }

    public void testAnnotatedMethodWithFutureProofExplicitRunner() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(SimpleTestWithFutureProofExplicitRunner.class);
        assertTrue(run);
    }

    static public class SetupTest {
        @Before
        public void before() {
            run = true;
        }

        @Test
        public void success() {
        }
    }

    public void testSetup() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(SetupTest.class);
        assertTrue(run);
    }

    static public class TeardownTest {
        @After
        public void after() {
            run = true;
        }

        @Test
        public void success() {
        }
    }

    public void testTeardown() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(TeardownTest.class);
        assertTrue(run);
    }

    static public class FailureTest {
        @Test
        public void error() throws Exception {
            org.junit.Assert.fail();
        }
    }

    public void testRunFailure() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(FailureTest.class);
        assertEquals(1, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(AssertionError.class, result.getFailures().get(0).getException().getClass());
    }

    static public class SetupFailureTest {
        @Before
        public void before() {
            throw new Error();
        }

        @Test
        public void test() {
            run = true;
        }
    }

    public void testSetupFailure() throws Exception {
        JUnitCore core = new JUnitCore();
        Result runner = core.run(SetupFailureTest.class);
        assertEquals(1, runner.getRunCount());
        assertEquals(1, runner.getFailureCount());
        assertEquals(Error.class, runner.getFailures().get(0).getException().getClass());
        assertFalse(run);
    }

    static public class TeardownFailureTest {
        @After
        public void after() {
            throw new Error();
        }

        @Test
        public void test() {
        }
    }

    public void testTeardownFailure() throws Exception {
        JUnitCore core = new JUnitCore();
        Result runner = core.run(TeardownFailureTest.class);
        assertEquals(1, runner.getRunCount());
        assertEquals(1, runner.getFailureCount());
        assertEquals(Error.class, runner.getFailures().get(0).getException().getClass());
    }

    static public class TestAndTeardownFailureTest {
        @After
        public void after() {
            throw new Error("hereAfter");
        }

        @Test
        public void test() throws Exception {
            throw new Exception("inTest");
        }
    }

    public void testTestAndTeardownFailure() throws Exception {
        JUnitCore core = new JUnitCore();
        Result runner = core.run(TestAndTeardownFailureTest.class);
        assertEquals(1, runner.getRunCount());
        assertEquals(2, runner.getFailureCount());
        assertThat(runner.getFailures().toString(), allOf(containsString("hereAfter"), containsString("inTest")));
    }

    static public class TeardownAfterFailureTest {
        @After
        public void after() {
            run = true;
        }

        @Test
        public void test() throws Exception {
            throw new Exception();
        }
    }

    public void testTeardownAfterFailure() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(TeardownAfterFailureTest.class);
        assertTrue(run);
    }

    static int count;
    static Collection<Object> tests;

    static public class TwoTests {
        @Test
        public void one() {
            count++;
            tests.add(this);
        }

        @Test
        public void two() {
            count++;
            tests.add(this);
        }
    }

    public void testTwoTests() throws Exception {
        count = 0;
        tests = new HashSet<Object>();
        JUnitCore runner = new JUnitCore();
        runner.run(TwoTests.class);
        assertEquals(2, count);
        assertEquals(2, tests.size());
    }

    static public class OldTest extends TestCase {
        public void test() {
            run = true;
        }
    }

    public void testOldTest() throws Exception {
        JUnitCore runner = new JUnitCore();
        runner.run(OldTest.class);
        assertTrue(run);
    }

    static public class OldSuiteTest extends TestCase {
        public void testOne() {
            run = true;
        }
    }

    public void testOldSuiteTest() throws Exception {
        TestSuite suite = new TestSuite(OldSuiteTest.class);
        JUnitCore runner = new JUnitCore();
        runner.run(suite);
        assertTrue(run);
    }

    static public class ExceptionTest {
        @Test(expected = Error.class)
        public void expectedException() {
            throw new Error();
        }
    }

    public void testException() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(ExceptionTest.class);
        assertEquals(0, result.getFailureCount());
    }

    static public class NoExceptionTest {
        @Test(expected = Error.class)
        public void expectedException() {
        }
    }

    public void testExceptionNotThrown() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(NoExceptionTest.class);
        assertEquals(1, result.getFailureCount());
        assertEquals("Expected exception: java.lang.Error", result.getFailures().get(0).getMessage());
    }

    static public class OneTimeSetup {
        @BeforeClass
        public static void once() {
            count++;
        }

        @Test
        public void one() {
        }

        @Test
        public void two() {
        }
    }

    public void testOneTimeSetup() throws Exception {
        count = 0;
        JUnitCore core = new JUnitCore();
        core.run(OneTimeSetup.class);
        assertEquals(1, count);
    }

    static public class OneTimeTeardown {
        @AfterClass
        public static void once() {
            count++;
        }

        @Test
        public void one() {
        }

        @Test
        public void two() {
        }
    }

    public void testOneTimeTeardown() throws Exception {
        count = 0;
        JUnitCore core = new JUnitCore();
        core.run(OneTimeTeardown.class);
        assertEquals(1, count);
    }

    static String log;

    public static class OrderTest {
        @BeforeClass
        public static void onceBefore() {
            log += "beforeClass ";
        }

        @Before
        public void before() {
            log += "before ";
        }

        @Test
        public void test() {
            log += "test ";
        }

        @After
        public void after() {
            log += "after ";
        }

        @AfterClass
        public static void onceAfter() {
            log += "afterClass ";
        }
    }

    public void testOrder() throws Exception {
        log = "";
        JUnitCore core = new JUnitCore();
        core.run(OrderTest.class);
        assertEquals("beforeClass before test after afterClass ", log);
    }

    static public class NonStaticOneTimeSetup {
        @BeforeClass
        public void once() {
        }

        @Test
        public void aTest() {
        }
    }

    public void testNonStaticOneTimeSetup() throws Exception {
        JUnitCore core = new JUnitCore();
        Result result = core.run(NonStaticOneTimeSetup.class);
        assertEquals(1, result.getFailureCount());
    }

    static public class ErrorInBeforeClass {
        @BeforeClass
        public static void before() throws Exception {
            throw new Exception();
        }

        @Test
        public void test() {
            run = true;
        }
    }

    public void testErrorInBeforeClass() throws Exception {
        run = false;
        JUnitCore core = new JUnitCore();
        Result result = core.run(ErrorInBeforeClass.class);
        assertFalse(run);
        assertEquals(1, result.getFailureCount());
        Description description = result.getFailures().get(0).getDescription();
        assertEquals(ErrorInBeforeClass.class.getName(), description.getDisplayName());
    }

    static public class ErrorInAfterClass {
        @Test
        public void test() {
            run = true;
        }

        @AfterClass
        public static void after() throws Exception {
            throw new Exception();
        }
    }

    public void testErrorInAfterClass() throws Exception {
        run = false;
        JUnitCore core = new JUnitCore();
        Result result = core.run(ErrorInAfterClass.class);
        assertTrue(run);
        assertEquals(1, result.getFailureCount());
    }

    static class SuperInheritance {
        @BeforeClass
        static public void beforeClassSuper() {
            log += "Before class super ";
        }

        @AfterClass
        static public void afterClassSuper() {
            log += "After class super ";
        }

        @Before
        public void beforeSuper() {
            log += "Before super ";
        }

        @After
        public void afterSuper() {
            log += "After super ";
        }
    }

    static public class SubInheritance extends SuperInheritance {
        @BeforeClass
        static public void beforeClassSub() {
            log += "Before class sub ";
        }

        @AfterClass
        static public void afterClassSub() {
            log += "After class sub ";
        }

        @Before
        public void beforeSub() {
            log += "Before sub ";
        }

        @After
        public void afterSub() {
            log += "After sub ";
        }

        @Test
        public void test() {
            log += "Test ";
        }
    }

    public void testOrderingOfInheritance() throws Exception {
        log = "";
        JUnitCore core = new JUnitCore();
        core.run(SubInheritance.class);
        assertEquals("Before class super Before class sub Before super Before sub Test After sub After super After class sub After class super ", log);
    }

    static public abstract class SuperShadowing {

        @Rule
        public TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    log += "super.rule().before() ";
                }

                @Override
                protected void after() {
                    log += "super.rule().after() ";
                }
            };
        }

        @Before
        public void before() {
            log += "super.before() ";
        }

        @After
        public void after() {
            log += "super.after() ";
        }
    }

    static public class SubShadowing extends SuperShadowing {

        @Override
        @Rule
        public TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    log += "sub.rule().before() ";
                }

                @Override
                protected void after() {
                    log += "sub.rule().after() ";
                }
            };
        }

        @Override
        @Before
        public void before() {
            super.before();
            log += "sub.before() ";
        }

        @Before
        public void anotherBefore() {
            log += "sub.anotherBefore() ";
        }

        @Override
        @After
        public void after() {
            log += "sub.after() ";
            super.after();
        }

        @After
        public void anotherAfter() {
            log += "sub.anotherAfter() ";
        }

        @Test
        public void test() {
            log += "Test ";
        }
    }

    public void testShadowing() throws Exception {
        log = "";
        assertThat(testResult(SubShadowing.class), isSuccessful());
        assertEquals(
                "sub.rule().before() sub.anotherBefore() super.before() sub.before() "
                + "Test "
                + "sub.anotherAfter() sub.after() super.after() sub.rule().after() ",
                log);
    }

    static public abstract class SuperStaticMethodShadowing {

        @ClassRule
        public static TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    log += "super.rule().before() ";
                }

                @Override
                protected void after() {
                    log += "super.rule().after() ";
                }
            };
        }
    }

    static public class SubStaticMethodShadowing extends SuperStaticMethodShadowing {

        @ClassRule
        public static TestRule rule() {
            return new ExternalResource() {
                @Override
                protected void before() throws Throwable {
                    log += "sub.rule().before() ";
                }

                @Override
                protected void after() {
                    log += "sub.rule().after() ";
                }
            };
        }

        @Test
        public void test() {
            log += "Test ";
        }
    }

    public void testStaticMethodsCanBeTreatedAsShadowed() throws Exception {
        log = "";
        assertThat(testResult(SubStaticMethodShadowing.class), isSuccessful());
        assertEquals(
                "sub.rule().before() "
                + "Test "
                + "sub.rule().after() ",
                log);
    }

    static public abstract class SuperFieldShadowing {

        @Rule
        public final TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                log += "super.rule.before() ";
            }

            @Override
            protected void after() {
                log += "super.rule.after() ";
            }
        };
    }

    static public class SubFieldShadowing extends SuperFieldShadowing {

        @Rule
        public final TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                log += "sub.rule.before() ";
            }

            @Override
            protected void after() {
                log += "sub.rule.after() ";
            }
        };

        @Test
        public void test() {
            log += "Test ";
        }
    }

    public void testFieldsShadowFieldsFromParent() throws Exception {
        log = "";
        assertThat(testResult(SubFieldShadowing.class), isSuccessful());
        assertEquals(
                "sub.rule.before() "
                + "Test "
                + "sub.rule.after() ",
                log);
    }

    static public abstract class SuperStaticFieldShadowing {

        @ClassRule
        public static TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                log += "super.rule.before() ";
            }

            @Override
            protected void after() {
                log += "super.rule.after() ";
            }
        };
    }

    static public class SubStaticFieldShadowing extends SuperStaticFieldShadowing {

        @ClassRule
        public static TestRule rule = new ExternalResource() {
            @Override
            protected void before() throws Throwable {
                log += "sub.rule.before() ";
            }

            @Override
            protected void after() {
                log += "sub.rule.after() ";
            }
        };

        @Test
        public void test() {
            log += "Test ";
        }
    }

    public void testStaticFieldsCanBeTreatedAsShadowed() throws Exception {
        log = "";
        assertThat(testResult(SubStaticFieldShadowing.class), isSuccessful());
        assertEquals(
                "sub.rule.before() "
                + "Test "
                + "sub.rule.after() ",
                log);
    }

    static public class SuperTest {
        @Test
        public void one() {
            log += "Super";
        }

        @Test
        public void two() {
            log += "Two";
        }
    }

    static public class SubTest extends SuperTest {
        @Override
        @Test
        public void one() {
            log += "Sub";
        }
    }

    public void testTestInheritance() throws Exception {
        log = "";
        JUnitCore core = new JUnitCore();
        core.run(SubTest.class);
        // The order in which the test methods are called is unspecified
        assertTrue(log.contains("Sub"));
        assertTrue(log.contains("Two"));
        assertFalse(log.contains("Super"));
    }

    static public class RunAllAfters {
        @Before
        public void good() {
        }

        @Before
        public void bad() {
            throw new Error();
        }

        @Test
        public void empty() {
        }

        @After
        public void one() {
            log += "one";
        }

        @After
        public void two() {
            log += "two";
        }
    }

    public void testRunAllAfters() {
        log = "";
        JUnitCore core = new JUnitCore();
        core.run(RunAllAfters.class);
        assertTrue(log.contains("one"));
        assertTrue(log.contains("two"));
    }

    static public class RunAllAftersRegardless {
        @Test
        public void empty() {
        }

        @After
        public void one() {
            log += "one";
            throw new Error();
        }

        @After
        public void two() {
            log += "two";
            throw new Error();
        }
    }

    public void testRunAllAftersRegardless() {
        log = "";
        JUnitCore core = new JUnitCore();
        Result result = core.run(RunAllAftersRegardless.class);
        assertTrue(log.contains("one"));
        assertTrue(log.contains("two"));
        assertEquals(2, result.getFailureCount());
    }

    static public class RunAllAfterClasses {
        @Before
        public void good() {
        }

        @BeforeClass
        public static void bad() {
            throw new Error();
        }

        @Test
        public void empty() {
        }

        @AfterClass
        public static void one() {
            log += "one";
        }

        @AfterClass
        public static void two() {
            log += "two";
        }
    }

    public void testRunAllAfterClasses() {
        log = "";
        JUnitCore core = new JUnitCore();
        core.run(RunAllAfterClasses.class);
        assertTrue(log.contains("one"));
        assertTrue(log.contains("two"));
    }

    static public class RunAllAfterClassesRegardless {
        @Test
        public void empty() {
        }

        @AfterClass
        static public void one() {
            log += "one";
            throw new Error();
        }

        @AfterClass
        static public void two() {
            log += "two";
            throw new Error();
        }
    }

    public void testRunAllAfterClassesRegardless() {
        log = "";
        JUnitCore core = new JUnitCore();
        Result result = core.run(RunAllAfterClassesRegardless.class);
        assertTrue(log.contains("one"));
        assertTrue(log.contains("two"));
        assertEquals(2, result.getFailureCount());
    }
}
