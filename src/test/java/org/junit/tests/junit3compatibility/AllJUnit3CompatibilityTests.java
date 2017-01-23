package org.junit.tests.junit3compatibility;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AllTestsTest.class,
        ClassRequestTest.class,
        ForwardCompatibilityPrintingTest.class,
        ForwardCompatibilityTest.class,
        InitializationErrorForwardCompatibilityTest.class,
        JUnit38ClassRunnerTest.class,
        JUnit4TestAdapterTest.class,
        OldTestClassAdaptingListenerTest.class,
        OldTests.class,
        SuiteMethodTest.class
})
public class AllJUnit3CompatibilityTests {
}
