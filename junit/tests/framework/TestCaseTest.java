package junit.tests.framework;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.tests.WasRun;

/**
 * A test case testing the testing framework.
 *
 */
public class TestCaseTest extends TestCase {
	
	static class TornDown extends TestCase {
		boolean fTornDown= false;
		
		protected void tearDown() {
			fTornDown= true;
		}
		protected void runTest() {
			throw new Error("running");
		}
	}

	public void testCaseToString() {
		// This test wins the award for twisted snake tail eating while
		// writing self tests. And you thought those weird anonymous
		// inner classes were bad...
		assertEquals("testCaseToString(junit.tests.framework.TestCaseTest)", toString());
	}
	public void testError() {
		TestCase error= new TestCase("error") {
			protected void runTest() {
				throw new Error();
			}
		};
		verifyError(error);
	}
	public void testRunAndTearDownFails() {
		TornDown fails= new TornDown() {
			protected void tearDown() {
				super.tearDown();
				throw new Error();
			}
			protected void runTest() {
				throw new Error();
			}
		};
		verifyError(fails);
		assertTrue(fails.fTornDown);
	}
	public void testSetupFails() {
		TestCase fails= new TestCase("success") {
			protected void setUp() {
				throw new Error();
			}
			protected void runTest() {
			}
		};
		verifyError(fails);
	}
	public void testSuccess() {
		TestCase success= new TestCase("success") {
			protected void runTest() {
			}
		};
		verifySuccess(success);
	}
	public void testFailure() {
		TestCase failure= new TestCase("failure") {
			protected void runTest() {
				fail();
			}
		};
		verifyFailure(failure);
	}

	public void testTearDownAfterError() {
		TornDown fails= new TornDown();
		verifyError(fails);
		assertTrue(fails.fTornDown);
	}
	
	public void testTearDownFails() {
		TestCase fails= new TestCase("success") {
			protected void tearDown() {
				throw new Error();
			}
			protected void runTest() {
			}
		};
		verifyError(fails);
	}
	public void testTearDownSetupFails() {
		TornDown fails= new TornDown() {
			protected void setUp() {
				throw new Error();
			}
		};
		verifyError(fails);
		assertTrue(!fails.fTornDown);
	}
	public void testWasRun() {
		WasRun test= new WasRun(); 
		test.run();
		assertTrue(test.fWasRun);
	}
	public void testExceptionRunningAndTearDown() {
		// With 1.4, we should
		// wrap the exception thrown while running with the exception thrown
		// while tearing down
		Test t= new TornDown() {
			public void tearDown() {
				throw new Error("tearingDown");
			}
		};
		TestResult result= new TestResult();
		t.run(result);
		TestFailure failure= (TestFailure) result.errors().nextElement();
		assertEquals("running", failure.thrownException().getMessage());
	}
	
	public void testErrorTearingDownDoesntMaskErrorRunning() {
		final Exception running= new Exception("Running");
		TestCase t= new TestCase() {
			protected void runTest() throws Throwable {
				throw running;
			}
			protected void tearDown() throws Exception {
				throw new Error("Tearing down");
			}
		};
		try {
			t.runBare();
		} catch (Throwable thrown) {
			assertSame(running, thrown);
		}
	}
	
	public void testNoArgTestCasePasses() {
		Test t= new TestSuite(NoArgTestCaseTest.class);
		TestResult result= new TestResult();
		t.run(result);
		assertTrue(result.runCount() == 1);
		assertTrue(result.failureCount() == 0);
		assertTrue(result.errorCount() == 0);
	}
	
	public void testNamelessTestCase() {
		TestCase t= new TestCase() {};
		try {
			t.run();
			fail();
		} catch (AssertionFailedError e) {
		}
	}
	
	void verifyError(TestCase test) {
		TestResult result= test.run();
		assertTrue(result.runCount() == 1);
		assertTrue(result.failureCount() == 0);
		assertTrue(result.errorCount() == 1);
	}
	void verifyFailure(TestCase test) {
		TestResult result= test.run();
		assertTrue(result.runCount() == 1);
		assertTrue(result.failureCount() == 1);
		assertTrue(result.errorCount() == 0);
	}
	void verifySuccess(TestCase test) {
		TestResult result= test.run();
		assertTrue(result.runCount() == 1);
		assertTrue(result.failureCount() == 0);
		assertTrue(result.errorCount() == 0);
	}
}