package junit.tests.runner;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.runner.BaseTestRunner;

public class StackFilterTest extends TestCase {
    String fFiltered;
    String fUnfiltered;

    @Override
    protected void setUp() {
        StringWriter swin = new StringWriter();
        PrintWriter pwin = new PrintWriter(swin);
        pwin.println("junit.framework.AssertionFailedError");
        pwin.println("\tat junit.framework.Assert.fail(Assert.java:144)");
        pwin.println("\tat junit.framework.Assert.assert(Assert.java:19)");
        pwin.println("\tat junit.framework.Assert.assert(Assert.java:26)");
        pwin.println("\tat MyTest.f(MyTest.java:13)");
        pwin.println("\tat MyTest.testStackTrace(MyTest.java:8)");
        pwin.println("\tat java.lang.reflect.Method.invoke(Native Method)");
        pwin.println("\tat junit.framework.TestCase.runTest(TestCase.java:156)");
        pwin.println("\tat junit.framework.TestCase.runBare(TestCase.java:130)");
        pwin.println("\tat junit.framework.TestResult$1.protect(TestResult.java:100)");
        pwin.println("\tat junit.framework.TestResult.runProtected(TestResult.java:118)");
        pwin.println("\tat junit.framework.TestResult.run(TestResult.java:103)");
        pwin.println("\tat junit.framework.TestCase.run(TestCase.java:121)");
        pwin.println("\tat junit.framework.TestSuite.runTest(TestSuite.java:157)");
        pwin.println("\tat junit.framework.TestSuite.run(TestSuite.java, Compiled Code)");
        pwin.println("\tat junit.swingui.TestRunner$17.run(TestRunner.java:669)");
        fUnfiltered = swin.toString();

        StringWriter swout = new StringWriter();
        PrintWriter pwout = new PrintWriter(swout);
        pwout.println("junit.framework.AssertionFailedError");
        pwout.println("\tat MyTest.f(MyTest.java:13)");
        pwout.println("\tat MyTest.testStackTrace(MyTest.java:8)");
        fFiltered = swout.toString();
    }

    public void testFilter() {
        assertEquals(fFiltered, BaseTestRunner.getFilteredTrace(fUnfiltered));
    }
}