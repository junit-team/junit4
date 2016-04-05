package org.junit.tests.experimental.theories;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.tests.experimental.theories.internal.AllTheoriesInternalTests;
import org.junit.tests.experimental.theories.runner.AllTheoriesRunnerTests;

@RunWith(Suite.class)
@SuiteClasses({
        AllTheoriesInternalTests.class,
        AllTheoriesRunnerTests.class,
        ParameterSignatureTest.class,
        TestedOnSupplierTest.class,
        AssumingInTheoriesTest.class,
        PotentialAssignmentTest.class
})
public class AllTheoriesTests {
}
