package org.junit.tests.running.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;

// Make sure System.exit works as expected. We've had problems with this on some platforms.
public class SystemExitTest {
	
	private static final int EXIT_CODE= 5;

	static public class Exit {
		public static void main(String[] args) {
			System.exit(EXIT_CODE);
		}
	}
	
	@Test public void failureCausesExitCodeOf1() throws Exception {
		String java= System.getProperty("java.home")+File.separator+"bin"+File.separator+"java";
		String classPath= getClass().getClassLoader().getResource(".").getFile() + File.pathSeparator + System.getProperty("java.class.path");
		String [] cmd= { java, "-cp", classPath, getClass().getName() + "$Exit"}; 
		Process process= Runtime.getRuntime().exec(cmd);
		InputStream input= process.getInputStream();
		while((input.read()) != -1); 
		assertEquals(EXIT_CODE, process.waitFor());
	}
}
