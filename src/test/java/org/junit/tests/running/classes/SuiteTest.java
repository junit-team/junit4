package org.junit.tests.running.classes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestResult;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

public class SuiteTest {
    public static class TestA {
        @Test
        public void pass() {
        }
    }

    public static class TestB {
        @Test
        public void fail() {
            Assert.fail();
        }
    }

    @RunWith(Suite.class)
    @SuiteClasses({TestA.class, TestB.class})
    public static class All {
    }

    @RunWith(Suite.class)
    @SuiteClasses(TestA.class)
    static class NonPublicSuite {
    }

    @RunWith(Suite.class)
    @SuiteClasses(TestA.class)
    static class NonPublicSuiteWithBeforeClass {
        @BeforeClass
        public static void doesNothing() {}
    }

    public static class InheritsAll extends All {
    }

    @Test
    public void ensureTestIsRun() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(All.class);
        assertEquals(2, result.getRunCount());
        assertEquals(1, result.getFailureCount());
    }

    @Test
    public void ensureInheritedTestIsRun() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(InheritsAll.class);
        assertEquals(2, result.getRunCount());
        assertEquals(1, result.getFailureCount());
    }

    @Test
    public void suiteTestCountIsCorrect() throws Exception {
        Runner runner = Request.aClass(All.class).getRunner();
        assertEquals(2, runner.testCount());
    }

    @Test
    public void suiteClassDoesNotNeedToBePublic() {
        JUnitCore core = new JUnitCore();
        Result result = core.run(NonPublicSuite.class);
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
    }

    @Test
    public void nonPublicSuiteClassWithBeforeClassPasses() {
        assertThat(testResult(NonPublicSuiteWithBeforeClass.class), isSuccessful());
    }

    @Test
    public void ensureSuitesWorkWithForwardCompatibility() {
        junit.framework.Test test = new JUnit4TestAdapter(All.class);
        TestResult result = new TestResult();
        test.run(result);
        assertEquals(2, result.runCount());
    }

    @Test
    public void forwardCompatibilityWorksWithGetTests() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(All.class);
        List<? extends junit.framework.Test> tests = adapter.getTests();
        assertEquals(2, tests.size());
    }

    @Test
    public void forwardCompatibilityWorksWithTestCount() {
        JUnit4TestAdapter adapter = new JUnit4TestAdapter(All.class);
        assertEquals(2, adapter.countTestCases());
    }


    private static String log = "";

    @RunWith(Suite.class)
    @SuiteClasses({TestA.class, TestB.class})
    public static class AllWithBeforeAndAfterClass {
        @BeforeClass
        public static void before() {
            log += "before ";
        }

        @AfterClass
        public static void after() {
            log += "after ";
        }
    }

    @Test
    public void beforeAndAfterClassRunOnSuite() {
        log = "";
        JUnitCore.runClasses(AllWithBeforeAndAfterClass.class);
        assertEquals("before after ", log);
    }

    @RunWith(Suite.class)
    public static class AllWithOutAnnotation {
    }

    @Test
    public void withoutSuiteClassAnnotationProducesFailure() {
        Result result = JUnitCore.runClasses(AllWithOutAnnotation.class);
        assertEquals(1, result.getFailureCount());
        String expected = String.format(
                "class '%s' must have a SuiteClasses annotation",
                AllWithOutAnnotation.class.getName());
        assertEquals(expected, result.getFailures().get(0).getMessage());
    }

    @RunWith(Suite.class)
    @SuiteClasses(InfiniteLoop.class)
    static public class InfiniteLoop {
    }

    @Test
    public void whatHappensWhenASuiteHasACycle() {
        Result result = JUnitCore.runClasses(InfiniteLoop.class);
        assertEquals(1, result.getFailureCount());
    }

    @RunWith(Suite.class)
    @SuiteClasses({BiInfiniteLoop.class, BiInfiniteLoop.class})
    static public class BiInfiniteLoop {
    }

    @Test
    public void whatHappensWhenASuiteHasAForkingCycle() {
        Result result = JUnitCore.runClasses(BiInfiniteLoop.class);
        assertEquals(2, result.getFailureCount());
    }

    // The interesting case here is that Hydra indirectly contains two copies of
    // itself (if it only contains one, Java's StackOverflowError eventually
    // bails us out)

    @RunWith(Suite.class)
    @SuiteClasses({Hercules.class})
    static public class Hydra {
    }

    @RunWith(Suite.class)
    @SuiteClasses({Hydra.class, Hydra.class})
    static public class Hercules {
    }

    @Test
    public void whatHappensWhenASuiteContainsItselfIndirectly() {
        Result result = JUnitCore.runClasses(Hydra.class);
        assertEquals(2, result.getFailureCount());
    }

    @RunWith(Suite.class)
    @SuiteClasses({})
    public class WithoutDefaultConstructor {
        public WithoutDefaultConstructor(int i) {

        }
    }

    @Test
    public void suiteShouldBeOKwithNonDefaultConstructor() throws Exception {
        Result result = JUnitCore.runClasses(WithoutDefaultConstructor.class);
        assertTrue(result.wasSuccessful());
    }

    @RunWith(Suite.class)
    public class NoSuiteClassesAnnotation {
    }

    @Test
    public void suiteShouldComplainAboutNoSuiteClassesAnnotation() {
        assertThat(testResult(NoSuiteClassesAnnotation.class), hasSingleFailureContaining("SuiteClasses"));
    }
}
