
package junit.tests.runner;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.runner.BaseTestRunner;

public class BaseTestRunnerTest extends TestCase {
	private ClassLoader fContextLoader;
	
	public class MockRunner extends BaseTestRunner {
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

	protected void setUp() throws Exception {
		super.setUp();
		// context class loader is a global variable and set by the JUnit
		// class loaders. We need to restore the value back, since the 
		// MockRunner changes this variable.
		fContextLoader= Thread.currentThread().getContextClassLoader(); 
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		Thread.currentThread().setContextClassLoader(fContextLoader);
	}

}
