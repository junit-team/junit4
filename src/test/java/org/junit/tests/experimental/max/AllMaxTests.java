package org.junit.tests.experimental.max;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        DescriptionTest.class,
        JUnit38SortingTest.class,
        MaxStarterTest.class
})
public class AllMaxTests {
}
