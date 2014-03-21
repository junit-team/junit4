package org.junit.tests.listening;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

public class UserStopTest {
    private RunNotifier notifier;

    @Before
    public void createNotifier() {
        notifier = new RunNotifier();
        notifier.pleaseStop();
    }

    @Test(expected = StoppedByUserException.class)
    public void userStop() {
        notifier.fireTestStarted(null);
    }

    public static class OneTest {
        @Test
        public void foo() {
        }
    }

    @Test(expected = StoppedByUserException.class)
    public void stopClassRunner() throws Exception {
        Request.aClass(OneTest.class).getRunner().run(notifier);
    }
}
