package junit.tests;

import junit.framework.*;
import java.io.*;

public class TextRunnerTest extends TestCase {
	public TextRunnerTest(String name) {
		super(name);
	}
	
	public void testFailure() throws Exception {
		execTest("junit.tests.Failure", -1);
	}

	public void testSuccess() throws Exception {
		execTest("junit.tests.Success", 0);
	}

	public void testError() throws Exception {
		execTest("junit.tests.BogusDude", -1);
	}
	
	void execTest(String testClass, int expected) throws Exception {
		String java= System.getProperty("java.home")+File.separator+"bin"+File.separator+"java";
		String cp= System.getProperty("java.class.path");
		String [] cmd= { java, "-cp", cp, "junit.textui.TestRunner", testClass}; 
		Process p= Runtime.getRuntime().exec(cmd);
		InputStream i= p.getInputStream();
		int b;
		while((b= i.read()) != -1) 
			; //System.out.write(b); 
		int rc= p.waitFor();
		assertEquals(expected, rc);
	}
		

}