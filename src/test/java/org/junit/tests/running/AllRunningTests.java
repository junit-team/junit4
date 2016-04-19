package org.junit.tests.running;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.tests.running.classes.AllClassesTests;
import org.junit.tests.running.core.AllCoreTests;
import org.junit.tests.running.methods.AllMethodsTests;

@RunWith(Suite.class)
@SuiteClasses({
        AllClassesTests.class,
        AllCoreTests.class,
        AllMethodsTests.class
})
public class AllRunningTests {
}
