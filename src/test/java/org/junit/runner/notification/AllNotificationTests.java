package org.junit.runner.notification;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ConcurrentRunNotifierTest.class,
        RunNotifierTest.class,
        SynchronizedRunListenerTest.class
})
public class AllNotificationTests {
}
