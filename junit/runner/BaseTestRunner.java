package junit.runner;

import junit.framework.*;
import java.lang.reflect.*;
import java.text.NumberFormat;
import java.io.*;
import java.util.*;

/**
 * Base class for all test runners.
 * This class was born live on stage in Sardinia during XP2000.
 */
public abstract class BaseTestRunner implements TestListener {
	static final String SUITE_METHODNAME= "suite";
	static Properties fPreferences;
	static int fMaxMessage= 200;
	
	protected TestSuiteLoader fTestLoader;
	
	/**
	 * Returns the Test corresponding to the given suite. This is
	 * a template method, subclasses override runFailed(), clearStatus().
	 */
	public Test getTest(String suiteClassName) {
		if (suiteClassName.length() <= 0) {
			clearStatus();
			return null;
		}
		Class testClass= null;
		try {
			testClass= loadSuiteClass(suiteClassName);
		} catch (NoClassDefFoundError e) {
			runFailed("Class definition \""+suiteClassName+"\" not found");
			return null;
		} catch(Exception e) {
			runFailed("Class \""+suiteClassName+"\" not found");
			return null;
		}
		Method suiteMethod= null;
		try {
			suiteMethod= testClass.getMethod(SUITE_METHODNAME, new Class[0]);
	 	} catch(Exception e) {
	 		// try to extract a test suite automatically
			clearStatus();			
			return new TestSuite(testClass);
		}
		Test test= null;
		try {
			test= (Test)suiteMethod.invoke(null, new Class[0]); // static method
			if (test == null)
				return test;
		} catch(Exception e) {
			runFailed("Could not invoke the suite() method");
			return null;
		}
		clearStatus();
		return test;
	}
	
	/**
	 * Returns the formatted string of the elapsed time.
	 */
	public String elapsedTimeAsString(long runTime) {
		return NumberFormat.getInstance().format((double)runTime/1000);
	}
	
	public String extractClassName(String className) {
		if(className.startsWith("Default package for")) 
			return className.substring(className.lastIndexOf(".")+1);
		return className;
	}
	
	/**
	 * Truncates a String to the maximum length.
	 */
	public static String truncate(String s) {
		if (s.length() > fMaxMessage)
			s= s.substring(0, fMaxMessage)+"...";
		return s;
	}
	
	/**
	 * Override to define how to handle a failed loading of
	 * a test suite.
	 */
	protected abstract void runFailed(String message);
	
	/**
	 * Returns the loaded Class for a suite name. 
	 */
	protected Class loadSuiteClass(String suiteClassName) throws ClassNotFoundException {
		return fTestLoader.load(suiteClassName);
	}
	
	/**
	 * Clears the status message.
	 */
	protected void clearStatus() { // Belongs in the GUI TestRunner class
	}
	
	/**
	 * Returns the loader to be used.
	 */
	public static TestSuiteLoader getLoader() {
		if (getPreference("loading").equals("true") && !inVAJava())
			return new ReloadingTestSuiteLoader();
		return new StandardTestSuiteLoader();
	}
	
	private static File getPreferencesFile() {
	 	String home= System.getProperty("user.home");
 		return new File(home, "junit.properties");
 	}
 	
 	private static void readPreferences() {
 		InputStream is= null;
 		try {
 			is= new FileInputStream(getPreferencesFile());
 			fPreferences= new Properties(fPreferences);
			fPreferences.load(is);
		} catch (IOException e) {
			try {
				if (is != null)
					is.close();
			} catch (IOException e1) {
			}
		}
 	}
 	
 	private static String getPreference(String key) {
 		return fPreferences.getProperty(key);
 	}
 	
 	private static int getPreference(String key, int dflt) {
 		String value= getPreference(key);
 		int intValue= dflt;
 		if (value == null)
 			return intValue;
 		try {
 			intValue= Integer.parseInt(value);
 	 	} catch (NumberFormatException ne) {
 		}
 		return intValue;
 	}

 	public static boolean inVAJava() {
		try {
			Class.forName("com.ibm.uvm.tools.DebugSupport");
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

 	{
 		fPreferences= new Properties();
 		fPreferences.setProperty("loading", "true");
  		readPreferences();
 		fMaxMessage= getPreference("maxmessage", fMaxMessage);
 	}
 	
}