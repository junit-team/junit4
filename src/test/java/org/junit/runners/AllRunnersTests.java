package org.junit.runners;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.AllModelTests;
import org.junit.runners.parameterized.AllParameterizedTests;

@RunWith(Suite.class)
@SuiteClasses({
        AllModelTests.class,
        AllParameterizedTests.class,
        RuleContainerTest.class,
        CustomBlockJUnit4ClassRunnerTest.class
})
public class AllRunnersTests {
}
