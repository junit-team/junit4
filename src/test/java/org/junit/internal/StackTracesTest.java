package org.junit.internal;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StackTracesTest {
    private static final String EOL = System.getProperty("line.separator", "\n");

    @Test
    public void trimStackTraceForJUnit4TestFailingInTestMethod() {
        String exceptionMessage = "java.lang.AssertionError: message" + EOL
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "" + EOL
                + "\tat org.junit.Assert.fail(Assert.java:88)" + EOL
                + "\tat com.example.Example.methodUnderTest(Example.java:18)" + EOL
                + "\tat com.example.ExampleTest.testMethodUnderTest(ExampleTest.java:11)" + EOL;
        String fullTrace = expectedTrace
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)" + EOL
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)" + EOL
                + "\tat java.lang.reflect.Method.invoke(Method.java:606)" + EOL
                + "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)" + EOL
                + "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)" + EOL
                + "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:52)" + EOL
                + "\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)" + EOL
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:87)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:1)" + EOL
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)" + EOL
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)" + EOL
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)" + EOL
                + "\tat org.junit.runners.ParentRunner.access$0(ParentRunner.java:284)" + EOL
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)" + EOL
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)" + EOL
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)" + EOL;

        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }

    @Test
    public void trimStackTraceForJUnit4TestFailingInBeforeMethod() {
        String exceptionMessage = "java.lang.AssertionError: message" + EOL
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "" + EOL
                + "\tat org.junit.Assert.fail(Assert.java:88)" + EOL
                + "\tat com.example.ExampleTest.beforeMethod(ExampleTest.java:28)" + EOL;
        String fullTrace = expectedTrace
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)" + EOL
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)" + EOL
                + "\tat java.lang.reflect.Method.invoke(Method.java:606)" + EOL
                + "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)" + EOL
                + "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)" + EOL
                + "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:52)" + EOL
                + "\tat org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:24)" + EOL
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:87)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:1)" + EOL
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)" + EOL
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)" + EOL
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)" + EOL
                + "\tat org.junit.runners.ParentRunner.access$0(ParentRunner.java:284)" + EOL
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)" + EOL
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)" + EOL
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)" + EOL;

        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
    
    @Test
    public void trimStackTraceForJUnit4TestFailingInRule() {
        String exceptionMessage = "java.lang.AssertionError: message" + EOL
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "" + EOL
                + "\tat org.junit.Assert.fail(Assert.java:88)" + EOL
                + "\tat org.example.ExampleRule$1.evaluate(ExampleRule.java:46)" + EOL;
        String fullTrace = expectedTrace
                + "\tat org.junit.rules.RunRules.evaluate(RunRules.java:20)" + EOL
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:87)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:1)" + EOL
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)" + EOL
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)" + EOL
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)" + EOL
                + "\tat org.junit.runners.ParentRunner.access$0(ParentRunner.java:284)" + EOL
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)" + EOL
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)" + EOL
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)" + EOL;
      
        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
    
    @Test
    public void trimStackTraceForJUnit3TestFailingInTestMethod() {
        String exceptionMessage = "java.lang.AssertionError: message" + EOL
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "" + EOL
                + "\tat junit.framework.TestCase.fail(TestCase.java:223)" + EOL
                + "\tat com.example.ExampleTest.testFilter(ExampleTest.java:48)" + EOL;
        String fullTrace = expectedTrace
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)" + EOL
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)" + EOL
                + "\tat java.lang.reflect.Method.invoke(Method.java:606)" + EOL
                + "\tat junit.framework.TestCase.runTest(TestCase.java:177)" + EOL
                + "\tat junit.framework.TestCase.runBare(TestCase.java:142)" + EOL
                + "\tat junit.framework.TestResult$1.protect(TestResult.java:125)" + EOL
                + "\tat junit.framework.TestResult.runProtected(TestResult.java:145)" + EOL
                + "\tat junit.framework.TestResult.run(TestResult.java:128)" + EOL
                + "\tat junit.framework.TestCase.run(TestCase.java:130)" + EOL
                + "\tat junit.framework.TestSuite.runTest(TestSuite.java:252)" + EOL
                + "\tat junit.framework.TestSuite.run(TestSuite.java:247)" + EOL
                + "\tat org.junit.internal.runners.JUnit38ClassRunner.run(JUnit38ClassRunner.java:86)" + EOL
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)" + EOL;

        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
    
    @Test
    public void trimStackTraceForJUnit3TestFailingInSetUpMethod() {
        String exceptionMessage = "java.lang.AssertionError: message" + EOL
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "" + EOL
                + "\tat junit.framework.Assert.fail(Assert.java:57)" + EOL
                + "\tat junit.framework.TestCase.fail(TestCase.java:223)" + EOL
                + "\tat com.example.ExampleTest.setUp(StackFilterTest.java:44)" + EOL;
        String fullTrace = expectedTrace
                + "\tat junit.framework.TestCase.runBare(TestCase.java:140)" + EOL
                + "\tat junit.framework.TestResult$1.protect(TestResult.java:125)" + EOL
                + "\tat junit.framework.TestResult.runProtected(TestResult.java:145)" + EOL
                + "\tat junit.framework.TestResult.run(TestResult.java:128)" + EOL
                + "\tat junit.framework.TestCase.run(TestCase.java:130)" + EOL
                + "\tat junit.framework.TestSuite.runTest(TestSuite.java:252)" + EOL
                + "\tat junit.framework.TestSuite.run(TestSuite.java:247)" + EOL
                + "\tat org.junit.internal.runners.JUnit38ClassRunner.run(JUnit38ClassRunner.java:86)" + EOL
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)" + EOL
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)" + EOL;
        
        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
}
