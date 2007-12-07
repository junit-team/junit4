package junit.tests.framework;

/**
 * Test class used in SuiteTest
 */
import junit.framework.TestCase;

public class NotVoidTestCase extends TestCase {
	public int testNotVoid() {
		return 1;
	}
	public void testVoid() {
	}
}