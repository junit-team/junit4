package org.junit.runners.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        FrameworkFieldTest.class,
        FrameworkMethodTest.class,
        InvalidTestClassErrorTest.class,
        TestClassTest.class
})
public class AllModelTests {
}
