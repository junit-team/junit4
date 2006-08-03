package junit.tests.framework;

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
		
		@Override
		protected void tearDown() {
			fTornDown= true;
		}
		@Override
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
			@Override
			protected void runTest() {
				throw new Error();
			}
		};
		verifyError(error);
	}
	public void testRunAndTearDownFails() {
		TornDown fails= new TornDown() {
			@Override
			protected void tearDown() {
				super.tearDown();
				throw new Error();
			}
			@Override
			protected void runTest() {
				throw new Error();
			}
		};
		verifyError(fails);
		assertTrue(fails.fTornDown);
	}
	public void testSetupFails() {
		TestCase fails= new TestCase("success") {
			@Override
			protected void setUp() {
				throw new Error();
			}
			@Override
			protected void runTest() {
			}
		};
		verifyError(fails);
	}
	public void testSuccess() {
		TestCase success= new TestCase("success") {
			@Override
			protected void runTest() {
			}
		};
		verifySuccess(success);
	}
	public void testFailure() {
		TestCase failure= new TestCase("failure") {
			@Override
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
			@Override
			protected void tearDown() {
				throw new Error();
			}
			@Override
			protected void runTest() {
			}
		};
		verifyError(fails);
	}
	public void testTearDownSetupFails() {
		TornDown fails= new TornDown() {
			@Override
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
			@Override
			public void tearDown() {
				throw new Error("tearingDown");
			}
		};
		TestResult result= new TestResult();
		t.run(result);
		TestFailure failure= result.errors().nextElement();
		assertEquals("running", failure.thrownException().getMessage());
	}
	
	public void testErrorTearingDownDoesntMaskErrorRunning() {
		final Exception running= new Exception("Running");
		TestCase t= new TestCase() {
			@Override
			protected void runTest() throws Throwable {
				throw running;
			}
			@Override
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
		TestResult result = t.run();
		assertEquals(1, result.failureCount());
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