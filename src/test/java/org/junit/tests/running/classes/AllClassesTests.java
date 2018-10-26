package org.junit.tests.running.classes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.tests.running.classes.parent.ParentRunnerClassLoaderTest;

@RunWith(Suite.class)
@SuiteClasses({
        BlockJUnit4ClassRunnerTest.class,
        ClassLevelMethodsWithIgnoredTestsTest.class,
        EnclosedTest.class,
        IgnoreClassTest.class,
        ParameterizedTestTest.class,
        ParentRunnerFilteringTest.class,
        ParentRunnerTest.class,
        ParentRunnerClassLoaderTest.class,
        RunWithTest.class,
        SuiteTest.class,
        UseSuiteAsASuperclassTest.class,
        ThreadsTest.class
})
public class AllClassesTests {
}
