package org.junit.tests.listening;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;

public class RunnerTest {

    private boolean wasRun;

    public class MyListener extends RunListener {

        int testCount;

        @Override
        public void testRunStarted(Description description) {
            this.testCount = description.testCount();
        }
    }

    public static class Example {
        @Test
        public void empty() {
        }
    }

    @Test
    public void newTestCount() {
        JUnitCore runner = new JUnitCore();
        MyListener listener = new MyListener();
        runner.addListener(listener);
        runner.run(Example.class);
        assertEquals(1, listener.testCount);
    }

    public static class ExampleTest extends TestCase {
        public void testEmpty() {
        }
    }

    @Test
    public void oldTestCount() {
        JUnitCore runner = new JUnitCore();
        MyListener listener = new MyListener();
        runner.addListener(listener);
        runner.run(ExampleTest.class);
        assertEquals(1, listener.testCount);
    }

    public static class NewExample {
        @Test
        public void empty() {
        }
    }

    @Test
    public void testFinished() {
        JUnitCore runner = new JUnitCore();
        wasRun = false;
        RunListener listener = new MyListener() {
            @Override
            public void testFinished(Description description) {
                wasRun = true;
            }
        };
        runner.addListener(listener);
        runner.run(NewExample.class);
        assertTrue(wasRun);
    }
}
