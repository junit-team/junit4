package junit.tests.framework;

import junit.framework.*;
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
			throw new Error();
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
		// This test documents the current behavior. With 1.4, we should
		// wrap the exception thrown while running with the exception thrown
		// while tearing down
		Test t= new TornDown() {
			public void tearDown() {
				throw new Error("tearDown");
			}
		};
		TestResult result= new TestResult();
		t.run(result);
		TestFailure failure= (TestFailure) result.errors().nextElement();
		assertEquals("tearDown", failure.thrownException().getMessage());
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