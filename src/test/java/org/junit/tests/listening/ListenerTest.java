package org.junit.tests.listening;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.RunListener;

public class ListenerTest {
    static private String log;

    public static class OneTest {
        @Test
        public void nothing() {
        }
    }

    @Test
    public void notifyListenersInTheOrderInWhichTheyAreAdded() {
        JUnitCore core = new JUnitCore();
        log = "";
        core.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                log += "first ";
            }
        });
        core.addListener(new RunListener() {
            @Override
            public void testRunStarted(Description description) throws Exception {
                log += "second ";
            }
        });
        core.run(OneTest.class);
        assertEquals("first second ", log);
    }
}
