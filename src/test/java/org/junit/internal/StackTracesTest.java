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
    public void trimStackTraceForJUnit4TestRunningOnMavenFailingInTestMethod() {
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
                + "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)" + EOL
                + "\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)" + EOL
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:87)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)" + EOL
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)" + EOL
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)" + EOL
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)" + EOL
                + "\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)" + EOL
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)" + EOL
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)" + EOL
                + "\tat org.junit.runners.Suite.runChild(Suite.java:128)" + EOL
                + "\tat org.junit.runners.Suite.runChild(Suite.java:27)" + EOL
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)" + EOL
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)" + EOL
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)" + EOL
                + "\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)" + EOL
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)" + EOL
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:363)" + EOL
                + "\tat junit.framework.JUnit4TestAdapter.run(JUnit4TestAdapter.java:38)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)" + EOL
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)" + EOL
                + "\tat java.lang.reflect.Method.invoke(Method.java:606)" + EOL
                + "\tat org.apache.maven.surefire.junit.JUnitTestSet.execute(JUnitTestSet.java:99)" + EOL
                + "\tat org.apache.maven.surefire.junit.JUnit3Provider.executeTestSet(JUnit3Provider.java:130)" + EOL
                + "\tat org.apache.maven.surefire.junit.JUnit3Provider.invoke(JUnit3Provider.java:107)" + EOL
                + "\tat org.apache.maven.surefire.booter.ForkedBooter.invokeProviderInSameClassLoader(ForkedBooter.java:203)" + EOL
                + "\tat org.apache.maven.surefire.booter.ForkedBooter.runSuitesInProcess(ForkedBooter.java:155)" + EOL
                + "\tat org.apache.maven.surefire.booter.ForkedBooter.main(ForkedBooter.java:103)" + EOL;

        assertEquals(expectedTrace, StackTraces.trimStackTrace(exceptionMessage, fullTrace));
    }

    @Test
    public void trimStackTraceForJUnit4TestRunningOnGradleFailingInTestMethod() {
        String exceptionMessage = "java.lang.AssertionError: message" + EOL
                + "with multiple lines";
        String expectedTrace = exceptionMessage + "" + EOL
                + "\tat org.junit.Assert.fail(Assert.java:88)" + EOL
                + "\tat com.example.Example.methodUnderTest(Example.java:18)" + EOL
                + "\tat com.example.ExampleTest.testMethodUnderTest(ExampleTest.java:11)" + EOL;
        String fullTrace = expectedTrace
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)" + EOL
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)" + EOL
                + "\tat java.lang.reflect.Method.invoke(Method.java:497)" + EOL
                + "\tat org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:47)" + EOL
                + "\tat org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)" + EOL
                + "\tat org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:44)" + EOL
                + "\tat org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)" + EOL
                + "\tat org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:271)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:70)" + EOL
                + "\tat org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)" + EOL
                + "\tat org.junit.runners.ParentRunner$3.run(ParentRunner.java:238)" + EOL
                + "\tat org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:63)" + EOL
                + "\tat org.junit.runners.ParentRunner.runChildren(ParentRunner.java:236)" + EOL
                + "\tat org.junit.runners.ParentRunner.access$000(ParentRunner.java:53)" + EOL
                + "\tat org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:229)" + EOL
                + "\tat org.junit.runners.ParentRunner.run(ParentRunner.java:309)" + EOL
                + "\tat org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86)" + EOL
                + "\tat org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49)" + EOL
                + "\tat org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:64)" + EOL
                + "\tat org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:50)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)" + EOL
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)" + EOL
                + "\tat java.lang.reflect.Method.invoke(Method.java:497)" + EOL
                + "\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)" + EOL
                + "\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)" + EOL
                + "\tat org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)" + EOL
                + "\tat org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)" + EOL
                + "\tat com.sun.proxy.$Proxy2.processTestClass(Unknown Source)" + EOL
                + "\tat org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:106)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)" + EOL
                + "\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)" + EOL
                + "\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)" + EOL
                + "\tat java.lang.reflect.Method.invoke(Method.java:497)" + EOL
                + "\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)" + EOL
                + "\tat org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)" + EOL
                + "\tat org.gradle.messaging.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:360)" + EOL
                + "\tat org.gradle.internal.concurrent.DefaultExecutorFactory$StoppableExecutorImpl$1.run(DefaultExecutorFactory.java:64)" + EOL
                + "\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)" + EOL
                + "\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)" + EOL
                + "\tat java.lang.Thread.run(Thread.java:745)" + EOL;

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
