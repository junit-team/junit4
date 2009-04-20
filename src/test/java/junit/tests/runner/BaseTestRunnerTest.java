
package junit.tests.runner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.runner.BaseTestRunner;

public class BaseTestRunnerTest extends TestCase {	
	public static class MockRunner extends BaseTestRunner {
		private boolean fRunFailed = false;
		
		@Override
		protected void runFailed(String message) {
			fRunFailed = true;
		}

		@Override
		public void testEnded(String testName) {
		}

		@Override
		public void testFailed(int status, Test test, Throwable t) {
		}

		@Override
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
	
	public static class DoesntExtendTestCase {
		public static Test suite() {
			return new TestSuite();
		}
	}
		
	public void testInvokeSuiteOnNonSubclassOfTestCase() {
		MockRunner runner= new MockRunner();
		runner.getTest(DoesntExtendTestCase.class.getName());
		assertFalse(runner.fRunFailed);
	}
}
