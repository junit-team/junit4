package org.junit.tests.experimental.theories.internal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AllMembersSupplierTest.class,
        ParameterizedAssertionErrorTest.class,
        SpecificDataPointsSupplierTest.class
})
public class AllTheoriesInternalTests {
}
