package org.junit.internal;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StackTracesTest {

    @Test
    public void trimStackTraceForJUnit4TestFailingInTestMethod() {
        String exceptionMessage = "java.lang.AssertionError: message\n"
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "\n"
                + "\tat org.junit.Assert.fail(Assert.java:88)\n"
                + "\tat com.example.Example.methodUnderTest(Example.java:18)\n"
                + "\tat com.example.ExampleTest.testMethodUnderTest(ExampleTest.java:11)\n";
        String fullTrace = expectedTrace
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n"
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
                + "\tat java.lang.reflect.Method.invoke(Method.java:606)\n"
                + "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n"
                + "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n"
                + "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:52)\n"
                + "\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)\n"
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)\n"
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:87)\n"
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:1)\n"
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)\n"
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)\n"
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)\n"
                + "\tat org.junit.runners.ParentRunner.access$0(ParentRunner.java:284)\n"
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)\n"
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)\n"
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)\n";

        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }

    @Test
    public void trimStackTraceForJUnit4TestFailingInBeforeMethod() {
        String exceptionMessage = "java.lang.AssertionError: message\n"
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "\n"
                + "\tat org.junit.Assert.fail(Assert.java:88)\n"
                + "\tat com.example.ExampleTest.beforeMethod(ExampleTest.java:28)\n";
        String fullTrace = expectedTrace
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n"
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
                + "\tat java.lang.reflect.Method.invoke(Method.java:606)\n"
                + "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)\n"
                + "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)\n"
                + "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:52)\n"
                + "\tat org.junit.internal.runners.statements.RunBefores.evaluate(RunBefores.java:24)\n"
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)\n"
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:87)\n"
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:1)\n"
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)\n"
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)\n"
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)\n"
                + "\tat org.junit.runners.ParentRunner.access$0(ParentRunner.java:284)\n"
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)\n"
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)\n"
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\n" + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)\n" + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)\n" + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)\n" + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)\n";

        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
    
    @Test
    public void trimStackTraceForJUnit4TestFailingInRule() {
        String exceptionMessage = "java.lang.AssertionError: message\n"
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "\n"
                + "\tat org.junit.Assert.fail(Assert.java:88)\n"
                + "\tat org.example.ExampleRule$1.evaluate(ExampleRule.java:46)\n";
        String fullTrace = expectedTrace
                + "\tat org.junit.rules.RunRules.evaluate(RunRules.java:20)\n"
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)\n"
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:87)\n"
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:1)\n"
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)\n"
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)\n"
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)\n"
                + "\tat org.junit.runners.ParentRunner.access$0(ParentRunner.java:284)\n"
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)\n"
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)\n"
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)\n";
      
        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
    
    @Test
    public void trimStackTraceForJUnit3TestFailingInTestMethod() {
        String exceptionMessage = "java.lang.AssertionError: message\n"
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "\n"
                + "\tat junit.framework.TestCase.fail(TestCase.java:223)\n"
                + "\tat com.example.ExampleTest.testFilter(ExampleTest.java:48)\n";
        String fullTrace = expectedTrace
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n"
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n"
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n"
                + "\tat java.lang.reflect.Method.invoke(Method.java:606)\n"
                + "\tat junit.framework.TestCase.runTest(TestCase.java:177)\n"
                + "\tat junit.framework.TestCase.runBare(TestCase.java:142)\n"
                + "\tat junit.framework.TestResult$1.protect(TestResult.java:125)\n"
                + "\tat junit.framework.TestResult.runProtected(TestResult.java:145)\n"
                + "\tat junit.framework.TestResult.run(TestResult.java:128)\n"
                + "\tat junit.framework.TestCase.run(TestCase.java:130)\n"
                + "\tat junit.framework.TestSuite.runTest(TestSuite.java:252)\n"
                + "\tat junit.framework.TestSuite.run(TestSuite.java:247)\n"
                + "\tat org.junit.internal.runners.JUnit38ClassRunner.run(JUnit38ClassRunner.java:86)\n"
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)\n";

        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
    
    @Test
    public void trimStackTraceForJUnit3TestFailingInSetUpMethod() {
        String exceptionMessage = "java.lang.AssertionError: message\n"
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "\n"
                + "\tat junit.framework.Assert.fail(Assert.java:57)\n"
                + "\tat junit.framework.TestCase.fail(TestCase.java:223)\n"
                + "\tat com.example.ExampleTest.setUp(StackFilterTest.java:44)\n";
        String fullTrace = expectedTrace
                + "\tat junit.framework.TestCase.runBare(TestCase.java:140)\n"
                + "\tat junit.framework.TestResult$1.protect(TestResult.java:125)\n"
                + "\tat junit.framework.TestResult.runProtected(TestResult.java:145)\n"
                + "\tat junit.framework.TestResult.run(TestResult.java:128)\n"
                + "\tat junit.framework.TestCase.run(TestCase.java:130)\n"
                + "\tat junit.framework.TestSuite.runTest(TestSuite.java:252)\n"
                + "\tat junit.framework.TestSuite.run(TestSuite.java:247)\n"
                + "\tat org.junit.internal.runners.JUnit38ClassRunner.run(JUnit38ClassRunner.java:86)\n"
                + "\tat org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:675)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)\n"
                + "\tat org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)\n";
        
        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }
}
