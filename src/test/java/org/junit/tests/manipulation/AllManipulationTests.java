package org.junit.tests.manipulation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        FilterableTest.class,
        FilterTest.class,
        OrderableTest.class,
        OrderWithTest.class,
        SingleMethodTest.class,
        SortableTest.class
})
public class AllManipulationTests {
}
