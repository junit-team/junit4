package junit.tests.runner;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import junit.runner.BaseTestRunner;
import junit.runner.tracefilter.Packages;
import junit.runner.tracefilter.TraceFilter;

public class StackFilterTest extends TestCase {
    
    String filtered;
    String unfiltered;

    @Override
    protected void setUp() {
        StringWriter swin = new StringWriter();
        PrintWriter pwin = new PrintWriter(swin);
        pwin.println("junit.framework.AssertionFailedError");
        pwin.println("	at junit.framework.Assert.fail(Assert.java:144)");
        pwin.println("	at junit.framework.Assert.assert(Assert.java:19)");
        pwin.println("	at junit.framework.Assert.assert(Assert.java:26)");
        pwin.println("	at MyTest.f(MyTest.java:13)");
        pwin.println("	at MyTest.testStackTrace(MyTest.java:8)");
        pwin.println("	at java.lang.reflect.Method.invoke(Native Method)");
        pwin.println("	at junit.framework.TestCase.runTest(TestCase.java:156)");
        pwin.println("	at junit.framework.TestCase.runBare(TestCase.java:130)");
        pwin.println("	at junit.framework.TestResult$1.protect(TestResult.java:100)");
        pwin.println("	at junit.framework.TestResult.runProtected(TestResult.java:118)");
        pwin.println("	at junit.framework.TestResult.run(TestResult.java:103)");
        pwin.println("	at junit.framework.TestCase.run(TestCase.java:121)");
        pwin.println("	at junit.framework.TestSuite.runTest(TestSuite.java:157)");
        pwin.println("	at junit.framework.TestSuite.run(TestSuite.java, Compiled Code)");
        pwin.println("	at junit.swingui.TestRunner$17.run(TestRunner.java:669)");
        unfiltered = swin.toString();

        StringWriter swout = new StringWriter();
        PrintWriter pwout = new PrintWriter(swout);
        pwout.println("junit.framework.AssertionFailedError");
        pwout.println("	at MyTest.f(MyTest.java:13)");
        pwout.println("	at MyTest.testStackTrace(MyTest.java:8)");
        filtered = swout.toString();
    }

    public void testFilter() {
        TraceFilter.setPackages(new Packages(new String[]{"junit", "java.lang.reflect"}));
        BaseTestRunner.setPreference("filterstack", "true");
        assertEquals(filtered, BaseTestRunner.getFilteredTrace(unfiltered));
    }
    
}