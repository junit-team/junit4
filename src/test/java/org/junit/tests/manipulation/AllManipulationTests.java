package org.junit.tests.manipulation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        FilterableTest.class,
        FilterTest.class,
        SingleMethodTest.class,
        SortableTest.class,
        OrderableTest.class,
        OrderWithTest.class
})
public class AllManipulationTests {
}
