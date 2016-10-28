package org.junit.tests.description;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AnnotatedDescriptionTest.class,
        SuiteDescriptionTest.class,
        TestDescriptionMethodNameTest.class,
        TestDescriptionTest.class
})
public class AllDescriptionTests {
}
