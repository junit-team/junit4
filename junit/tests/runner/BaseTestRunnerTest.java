
package junit.tests.runner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.runner.BaseTestRunner;

public class BaseTestRunnerTest extends TestCase {
	
	public static class MockRunner extends BaseTestRunner {
		protected void runFailed(String message) {
		}

		public void testEnded(String testName) {
		}

		public void testFailed(int status, Test test, Throwable t) {
		}

		public void testStarted(String testName) {
		}
	}
	
	public static class NonStatic {
		public Test suite() {
			return null;
		}
	}

	
	public void testInvokeNonStaticSuite() {
		BaseTestRunner runner= new MockRunner();
		runner.getTest("junit.tests.runner.BaseTestRunnerTest$NonStatic"); // Used to throw NullPointerException
	}
}
