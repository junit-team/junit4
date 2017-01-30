package org.junit.tests.experimental.parallel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ParallelClassTest.class,
        ParallelMethodTest.class
})
public class AllParallelTests {
}
