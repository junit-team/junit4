
package junit.tests.runner;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.ResultPrinter;
import junit.textui.TestRunner;

public class TextFeedbackTest extends TestCase {
	OutputStream output;
	TestRunner runner;
	
	class TestResultPrinter extends ResultPrinter {
		TestResultPrinter(PrintStream writer) {
			super(writer);
		}
		
		/* Spoof printing time so the tests are deterministic
		 */
		protected String elapsedTimeAsString(long runTime) {
			return "0";
		}
	}
	
	public static void main(String[] args) {
		TestRunner.run(TextFeedbackTest.class);
	}
	
	public void setUp() {
		output= new ByteArrayOutputStream();
		runner= new TestRunner(new TestResultPrinter(new PrintStream(output)));
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
	
	public void testTwoTests() {
		String expected= expected(new String[]{"..", "Time: 0", "", "OK (2 tests)", ""});
		TestSuite suite = new TestSuite();
		suite.addTest(new TestCase() { public void runTest() {}});
		suite.addTest(new TestCase() { public void runTest() {}});
		runner.doRun(suite);
		assertEquals(expected.toString(), output.toString());
	}

	public void testFailure() {
		String expected= expected(new String[]{".F", "Time: 0", "Failures here", "", "FAILURES!!!", "Tests run: 1,  Failures: 1,  Errors: 0", ""});
		ResultPrinter printer= new TestResultPrinter(new PrintStream(output)) {
			public void printFailures(TestResult result) {
				getWriter().println("Failures here");
			}
		};
		runner.setPrinter(printer);
		TestSuite suite = new TestSuite();
		suite.addTest(new TestCase() { public void runTest() {throw new AssertionFailedError();}});
		runner.doRun(suite);
		assertEquals(expected.toString(), output.toString());
	}
	
	public void testError() {
		String expected= expected(new String[]{".E", "Time: 0", "Errors here", "", "FAILURES!!!", "Tests run: 1,  Failures: 0,  Errors: 1", ""});
		ResultPrinter printer= new ResultPrinter(new PrintStream(output)) {
			public void printErrors(TestResult result) {
				getWriter().println("Errors here");
			}
		};
		runner.setPrinter(printer);
		TestSuite suite = new TestSuite();
		suite.addTest(new TestCase() { public void runTest() throws Exception {throw new Exception();}});
		runner.doRun(suite);
		assertEquals(expected.toString(), output.toString());
	}
	
	public void testComparisonErrorMessage() {
		ComparisonFailure failure= new ComparisonFailure("a", "b", "c");
		assertEquals("a: expected:<b> but was:<c>", failure.getMessage());
	}
	
	public void testComparisonErrorStartSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "ba", "bc");
		assertEquals("expected:<...a> but was:<...c>", failure.getMessage());
	}
		
	public void testComparisonErrorEndSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "ab", "cb");
		assertEquals("expected:<a...> but was:<c...>", failure.getMessage());
	} 

	public void testComparisonErrorSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "ab", "ab");
		assertEquals("expected:<ab> but was:<ab>", failure.getMessage());
	} 

	public void testComparisonErrorStartAndEndSame() {
		ComparisonFailure failure= new ComparisonFailure(null, "abc", "adc");
		assertEquals("expected:<...b...> but was:<...d...>", failure.getMessage());
	} 

	public void testComparisonErrorStartSameComplete() {
		ComparisonFailure failure= new ComparisonFailure(null, "ab", "abc");
		assertEquals("expected:<...> but was:<...c>", failure.getMessage());
	} 

	public void testComparisonErrorEndSameComplete() {
		ComparisonFailure failure= new ComparisonFailure(null, "bc", "abc");
		assertEquals("expected:<...> but was:<a...>", failure.getMessage());
	} 

	public void testComparisonErrorOverlapingMatches() {
		ComparisonFailure failure= new ComparisonFailure(null, "abc", "abbc");
		assertEquals("expected:<......> but was:<...b...>", failure.getMessage());
	} 

	public void testComparisonErrorOverlapingMatches2() {
		ComparisonFailure failure= new ComparisonFailure(null, "abcdde", "abcde");
		assertEquals("expected:<...d...> but was:<......>", failure.getMessage());
	} 
	private String expected(String[] lines) {
		OutputStream expected= new ByteArrayOutputStream();
		PrintStream expectedWriter= new PrintStream(expected);
		for (int i= 0; i < lines.length; i++)
			expectedWriter.println(lines[i]);
		return expected.toString(); 
	}

}
