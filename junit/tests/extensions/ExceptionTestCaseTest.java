package junit.tests.extensions;

import junit.extensions.ExceptionTestCase;
import junit.framework.TestResult;

public class ExceptionTestCaseTest extends junit.framework.TestCase {

	static public class ThrowExceptionTestCase extends ExceptionTestCase {
		public ThrowExceptionTestCase(String name, Class exception) {
			super(name, exception);
		}
		public void test() {
			throw new IndexOutOfBoundsException();
		}
	}

	static public class ThrowRuntimeExceptionTestCase extends ExceptionTestCase {
		public ThrowRuntimeExceptionTestCase(String name, Class exception) {
			super(name, exception);
		}
		public void test() {
			throw new RuntimeException();
		}
	}

	static public class ThrowNoExceptionTestCase extends ExceptionTestCase {
		public ThrowNoExceptionTestCase(String name, Class exception) {
			super(name, exception);
		}
		public void test() {
		}
	}

	public void testExceptionSubclass() {
		ExceptionTestCase test= new ThrowExceptionTestCase("test", IndexOutOfBoundsException.class);
		TestResult result= test.run();
		assertEquals(1, result.runCount());
		assertTrue(result.wasSuccessful());
	}
	public void testExceptionTest() {
		ExceptionTestCase test= new ThrowExceptionTestCase("test", IndexOutOfBoundsException.class);
		TestResult result= test.run();
		assertEquals(1, result.runCount());
		assertTrue(result.wasSuccessful());
	}
	public void testFailure() {
		ExceptionTestCase test= new ThrowRuntimeExceptionTestCase("test", IndexOutOfBoundsException.class);
		TestResult result= test.run();
		assertEquals(1, result.runCount());
		assertEquals(1, result.errorCount());
	}
	public void testNoException() {
		ExceptionTestCase test= new ThrowNoExceptionTestCase("test", Exception.class);
		TestResult result= test.run();
		assertEquals(1, result.runCount());
		assertEquals(1, result.failureCount());
	}
	public void testWrongException() {
		ExceptionTestCase test= new ThrowRuntimeExceptionTestCase("test", IndexOutOfBoundsException.class);
		TestResult result= test.run();
		assertEquals(1, result.runCount());
		assertEquals(1, result.errorCount());
	}
}