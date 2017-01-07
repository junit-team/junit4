package org.junit.tests.listening;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ListenerTest.class,
        RunnerTest.class,
        TestListenerTest.class,
        TextListenerTest.class,
        UserStopTest.class
})
public class AllListeningTests {
}
