package org.junit.tests.experimental.max;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.max.MaxCore;
import org.junit.runner.Description;
import org.junit.runner.Request;

public class JUnit38SortingTest {
    private MaxCore max;
    private File maxFile;

    @Before
    public void createMax() {
        maxFile = new File("MaxCore.ser");
        if (maxFile.exists()) {
            maxFile.delete();
        }
        max = MaxCore.storedLocally(maxFile);
    }

    @After
    public void forgetMax() {
        maxFile.delete();
    }

    public static class JUnit4Test {
        @Test
        public void pass() {
        }
    }

    public static class JUnit38Test extends TestCase {
        public void testFails() {
            fail();
        }

        public void testSucceeds() {
        }

        public void testSucceedsToo() {
        }
    }

    @Test
    public void preferRecentlyFailed38Test() {
        Request request = Request.classes(JUnit4Test.class, JUnit38Test.class);
        max.run(request);
        List<Description> tests = max.sortedLeavesForTest(request);
        Description dontSucceed = Description.createTestDescription(
                JUnit38Test.class, "testFails");
        assertEquals(dontSucceed, tests.get(0));
    }

}
