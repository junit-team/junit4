package org.junit.tests.junit3compatibility;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

public class JUnit4TestAdapterTest {

    private static void doTest(Class<?> clazz) {
        // JUnit 4 runner:
        Result result = JUnitCore.runClasses(clazz);
        assertEquals(1, result.getRunCount());
        assertEquals(0, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());

        // JUnit 3 runner:
        TestResult testResult = new TestResult();
        new JUnit4TestAdapter(clazz).run(testResult);
        assertEquals(1, testResult.runCount());
        assertEquals(0, testResult.failureCount());
        assertEquals(Collections.emptyList(), Collections.list(testResult.errors()));
    }

    public static class Test4 {
        @Test
        public void pass() throws Exception {
            //pass
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(Test4.class)
    public static class TestSuiteFor4 {
    }

    @Test
    public void testJUnit4Suite() {
        doTest(TestSuiteFor4.class);
    }

    public static class Test3 extends TestCase {
        public void testPass() throws Exception {
            //pass
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(Test3.class)
    public static class TestSuiteFor3 {
    }

    @Test
    public void testJUnit3Suite() {
        doTest(TestSuiteFor3.class);
    }

    public static class TestSuite3 {
        public static junit.framework.Test suite() {
            return new TestSuite(Test3.class);
        }
    }

    @RunWith(Suite.class)
    @Suite.SuiteClasses(TestSuite3.class)
    public static class TestSuite4ForTestSuite3 {
    }

    @Test
    public void testJUnit4SuiteThatContainsJUnit3SuiteClass() {
        doTest(TestSuite4ForTestSuite3.class);
    }
}
