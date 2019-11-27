package junit.tests.runner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class TextRunnerTest extends TestCase {

    public void testFailure() throws Exception {
        execTest("junit.tests.framework.Failure", false);
    }

    public void testSuccess() throws Exception {
        execTest("junit.tests.framework.Success", true);
    }

    public void testError() throws Exception {
        execTest("junit.tests.BogusDude", false);
    }

    void execTest(String testClass, boolean success) throws Exception {
        String java = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        String cp = System.getProperty("java.class.path");
        //use -classpath for JDK 1.1.7 compatibility
        String[] cmd = {java, "-classpath", cp, "junit.textui.TestRunner", testClass};
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream i = p.getInputStream();
        while ((i.read()) != -1)
            ;
        assertTrue((p.waitFor() == 0) == success);
        if (success) {
            assertTrue(p.exitValue() == 0);
        } else {
            assertFalse(p.exitValue() == 0);
        }
    }

    public void testRunReturnsResult() {
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(
                new OutputStream() {
                    @Override
                    public void write(int arg0) throws IOException {
                    }
                }
        ));
        try {
            TestResult result = junit.textui.TestRunner.run(new TestSuite());
            assertTrue(result.wasSuccessful());
        } finally {
            System.setOut(oldOut);
        }
    }


}