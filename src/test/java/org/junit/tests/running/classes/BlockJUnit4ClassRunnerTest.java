package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class BlockJUnit4ClassRunnerTest {
    public static class OuterClass {
        public class Enclosed {
            @Test
            public void test() {
            }
        }
    }

    @Test
    public void detectNonStaticEnclosedClass() throws Exception {
        try {
            new BlockJUnit4ClassRunner(OuterClass.Enclosed.class);
        } catch (InitializationError e) {
            List<Throwable> causes = e.getCauses();
            assertEquals("Wrong number of causes.", 1, causes.size());
            assertEquals(
                    "Wrong exception.",
                    "The inner class org.junit.tests.running.classes.BlockJUnit4ClassRunnerTest$OuterClass$Enclosed is not static.",
                    causes.get(0).getMessage());
        }
    }

    private static String log;

    public static class MethodBlockAfterFireTestStarted {
        public MethodBlockAfterFireTestStarted() {
            log += " init";
        }

        @Test
        public void test() {
            log += " test";
        }
    }

    @Test
    public void methodBlockAfterFireTestStarted() {
        log = "";
        JUnitCore junit = new JUnitCore();
        junit.addListener(new RunListener() {
            @Override
            public void testStarted(Description description) throws Exception {
                log += " testStarted(" + description.getMethodName() + ")";
            }

            @Override
            public void testFinished(Description description) throws Exception {
                log += " testFinished(" + description.getMethodName() + ")";
            }
        });
        junit.run(MethodBlockAfterFireTestStarted.class);
        assertEquals(" testStarted(test) init test testFinished(test)", log);
    }
}
