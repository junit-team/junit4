package junit.tests.extensions;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.tests.WasRun;

/**
 * A test case testing the extensions to the testing framework.
 *
 */
public class ExtensionTest extends TestCase {
	static class TornDown extends TestSetup { 
		boolean fTornDown= false;
		
		TornDown(Test test) {
			super(test);
		}
		protected void tearDown() {
			fTornDown= true;
		}
	}
	public void testRunningErrorInTestSetup() {
		TestCase test= new TestCase("failure") {
			public void runTest() {
				fail();
			}
		};

		TestSetup wrapper= new TestSetup(test);

		TestResult result= new TestResult();
		wrapper.run(result);
		assertTrue(!result.wasSuccessful());
	}
	public void testRunningErrorsInTestSetup() {
		TestCase failure= new TestCase("failure") {
			public void runTest() {
				fail();
			}
		};

		TestCase error= new TestCase("error") {
			public void runTest() {
				throw new Error();
			}
		};

		TestSuite suite= new TestSuite();
		suite.addTest(failure);
		suite.addTest(error);
		
		TestSetup wrapper= new TestSetup(suite);

		TestResult result= new TestResult();
		wrapper.run(result);

		assertEquals(1, result.failureCount());
		assertEquals(1, result.errorCount());
	}
	public void testSetupErrorDontTearDown() {
		WasRun test= new WasRun();

		TornDown wrapper= new TornDown(test) {
			public void setUp() {
				fail();
			}
		};

		TestResult result= new TestResult();
		wrapper.run(result);

		assertTrue(!wrapper.fTornDown);
	}
	public void testSetupErrorInTestSetup() {
		WasRun test= new WasRun();

		TestSetup wrapper= new TestSetup(test) {
			public void setUp() {
				fail();
			}
		};

		TestResult result= new TestResult();
		wrapper.run(result);

		assertTrue(!test.fWasRun);
		assertTrue(!result.wasSuccessful());
	}
}