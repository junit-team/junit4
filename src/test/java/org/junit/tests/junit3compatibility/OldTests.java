package org.junit.tests.junit3compatibility;

import junit.framework.Test;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

@RunWith(AllTests.class)
public class OldTests {
    public static Test suite() {
        return junit.tests.AllTests.suite();
    }
}
