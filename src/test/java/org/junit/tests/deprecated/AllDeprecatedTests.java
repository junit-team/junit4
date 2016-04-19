package org.junit.tests.deprecated;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuppressWarnings("deprecation")
@RunWith(Suite.class)
@SuiteClasses({
        JUnit4ClassRunnerTest.class
})
public class AllDeprecatedTests {
}
