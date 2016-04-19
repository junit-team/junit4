package org.junit.runners.parameterized;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        BlockJUnit4ClassRunnerWithParametersTest.class,
        ParameterizedNamesTest.class,
        TestWithParametersTest.class
})
public class AllParameterizedTests {
}
