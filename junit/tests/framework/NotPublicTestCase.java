package junit.tests.framework;

/**
 * Test class used in SuiteTest
 */
import junit.framework.TestCase;

public class NotPublicTestCase extends TestCase {
	protected void testNotPublic() {
	}
	public void testPublic() {
	}
}