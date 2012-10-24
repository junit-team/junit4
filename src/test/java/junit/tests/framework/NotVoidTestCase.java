package junit.tests.framework;

import junit.framework.TestCase;

/**
 * Test class used in SuiteTest
 */
public class NotVoidTestCase extends TestCase {
    public int testNotVoid() {
        return 1;
    }

    public void testVoid() {
    }
}