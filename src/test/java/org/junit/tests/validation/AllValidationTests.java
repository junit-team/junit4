package org.junit.tests.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        BadlyFormedClassesTest.class,
        FailedConstructionTest.class,
        ValidationTest.class
})
public class AllValidationTests {
}
