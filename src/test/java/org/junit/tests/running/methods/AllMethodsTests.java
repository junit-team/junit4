package org.junit.tests.running.methods;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AnnotationTest.class,
        ExpectedTest.class,
        InheritedTestTest.class,
        ParameterizedTestMethodTest.class,
        TestMethodTest.class,
        TimeoutTest.class
})
public class AllMethodsTests {
}
