package org.junit.internal.builders;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.SuiteMethod;
import org.junit.runner.Runner;

import static org.junit.Assert.assertEquals;

public class AllDefaultPossibilitiesBuilderTest {
    @Test
    public void runnerShouldBeIgnoredClassRunner() throws Throwable {
        final AllDefaultPossibilitiesBuilder builder = new AllDefaultPossibilitiesBuilder(true);

        final Runner runner = builder.runnerForClass(IgnoredTestClass.class);

        assertEquals(IgnoredClassRunner.class, runner.getClass());
    }

    @Test
    public void runnerShouldNotBeWrappedInFilteredClassRunner() throws Throwable {
        final AllDefaultPossibilitiesBuilder builder = new AllDefaultPossibilitiesBuilder(true);

        final Runner runner = builder.runnerForClass(JUnit38TestClass.class);

        assertEquals(SuiteMethod.class, runner.getClass());
    }

    @Ignore
    public static class IgnoredTestClass {
        @Test
        public void dummyTest() {
        }
    }

    public static class BlockJUnit4TestClass {
        @Test
        public void dummyTest() {
        }
    }

    public static class JUnit38TestClass {
        public static Test suite() {
            return null;
        }
    }
}
