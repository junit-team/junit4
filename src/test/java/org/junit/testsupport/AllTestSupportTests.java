package org.junit.testsupport;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventCollectorMatchersTest.class,
        EventCollectorTest.class
})
public class AllTestSupportTests {
}
