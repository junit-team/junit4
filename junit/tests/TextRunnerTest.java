package junit.tests;

import junit.framework.*;
import java.io.*;

public class TextRunnerTest extends TestCase {
	public TextRunnerTest(String name) {
		super(name);
	}
	
	public void testFailure() throws Exception {
		execTest("junit.tests.Failure", false);
	}

	public void testSuccess() throws Exception {
		execTest("junit.tests.Success", true);
	}

	public void testError() throws Exception {
		execTest("junit.tests.BogusDude", false);
	}
	
	void execTest(String testClass, boolean success) throws Exception {
		String java= System.getProperty("java.home")+File.separator+"bin"+File.separator+"java";
		String cp= System.getProperty("java.class.path");
		//use -classpath for JDK 1.1.7 compatibility
		String [] cmd= { java, "-classpath", cp, "junit.textui.TestRunner", testClass}; 
		Process p= Runtime.getRuntime().exec(cmd);
		InputStream i= p.getInputStream();
		int b;
		while((b= i.read()) != -1) 
			; //System.out.write(b); 
		assertTrue((p.waitFor() == 0) == success);
		if (success)
			assertEquals(junit.textui.TestRunner.SUCCESS_EXIT, p.exitValue());
		else
			assertEquals(junit.textui.TestRunner.FAILURE_EXIT, p.exitValue());
	}
		

}