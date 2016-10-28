package org.junit.tests.running.core;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        CommandLineTest.class,
        JUnitCoreReturnsCorrectExitCodeTest.class,
        SystemExitTest.class
})
public class AllCoreTests {
}
