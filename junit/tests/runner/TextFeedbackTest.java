
package junit.tests.runner;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TextFeedbackTest extends TestCase {
	OutputStream output;
	TestRunner runner;
	
	public static void main(String[] args) {
		TestRunner.run(TextFeedbackTest.class);
	}
	
	public void setUp() {
		output= new ByteArrayOutputStream();
		runner= new TestRunner(new PrintStream(output));
	}
	
	public void testEmptySuite() {
		String expected= expected(new String[]{"", "Time: 0", "", "OK (0 tests)", ""});
		runner.doRun(new TestSuite());
		assertEquals(expected.toString(), output.toString());
	}

	
	public void testOneTest() {
		String expected= expected(new String[]{".", "Time: 0", "", "OK (1 test)", ""});
		TestSuite suite = new TestSuite();
		suite.addTest(new TestCase() { public void runTest() {}});
		runner.doRun(suite);
		assertEquals(expected.toString(), output.toString());
	}
	
	/* I can't think of a good way to test this until after the formatting
	 * has been extracted into its own object and we can spoof it
	
	public void testFailure() {
		String expected= expected(new String[]{".F", "Time: 0", "", "OK (1 test)", ""});
		TestSuite suite = new TestSuite();
		suite.addTest(new TestCase() { public void runTest() {throw new AssertionFailedError();}});
		runner.doRun(suite);
		assertEquals(expected.toString(), output.toString());
	}
	*/
	
	public void testTwoTests() {
		String expected= expected(new String[]{"..", "Time: 0", "", "OK (2 tests)", ""});
		TestSuite suite = new TestSuite();
		suite.addTest(new TestCase() { public void runTest() {}});
		suite.addTest(new TestCase() { public void runTest() {}});
		runner.doRun(suite);
		assertEquals(expected.toString(), output.toString());
	}
	
	private String expected(String[] lines) {
		OutputStream expected= new ByteArrayOutputStream();
		PrintStream expectedWriter= new PrintStream(expected);
		for (int i= 0; i < lines.length; i++)
			expectedWriter.println(lines[i]);
		return expected.toString();
	}

}
