
package junit.tests.runner;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.Collator;
import java.text.RuleBasedCollator;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TextFeedbackTest extends TestCase {
	public static void main(String[] args) {
		TestRunner.run(TextFeedbackTest.class);
	}
	
	public void testNormalOperation() {
		OutputStream output= new ByteArrayOutputStream();
		PrintStream writer= new PrintStream(output);
		String expected= expected(new String[]{"", "Time: 0", "", "OK (0 tests)", ""});
		TestRunner runner= new TestRunner(writer);
		runner.doRun(new TestSuite());
		Collator c= Collator.getInstance(); 
		assertEquals(expected.toString(), output.toString());
	}
	
	private String expected(String[] lines) {
		OutputStream expected= new ByteArrayOutputStream();
		PrintStream expectedWriter= new PrintStream(expected);
		expectedWriter.println();
		expectedWriter.println("Time: 0");
		expectedWriter.println();
		expectedWriter.println("OK (0 tests)");
		expectedWriter.println();
		return expected.toString();
	}

}
