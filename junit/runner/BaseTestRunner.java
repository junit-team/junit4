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
	public static final String SUITE_METHODNAME= "suite";
	
	static Properties fPreferences;
	static int fMaxMessageLength= 200;
	static boolean filterStack;
	boolean fLoading= true;
	
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
	
	/**
	 * Processes the command line arguments and
	 * returns the name of the suite class to run or null
	 */
	protected String processArguments(String[] args) {
		String suiteName= null;
		for (int i= 0; i < args.length; i++) {
			if (args[i].equals("-noloading")) {
				setLoading(false);
			} else if (args[i].equals("-c")) {
				if (args.length > i+1)
					suiteName= extractClassName(args[i+1]);
				else
					System.out.println("Missing Test class name");
				i++;
			} else {
				suiteName= args[i];
			}
		}
		return suiteName;		
	}

	/**
	 * Sets the loading behaviour of the test runner
	 */
	protected void setLoading(boolean enable) {
		fLoading= enable;
	}
	/**
	 * Extract the class name from a String in VA/Java style
	 */
	public String extractClassName(String className) {
		if(className.startsWith("Default package for")) 
			return className.substring(className.lastIndexOf(".")+1);
		return className;
	}
	
	/**
	 * Truncates a String to the maximum length.
	 */
	public static String truncate(String s) {
		if (fMaxMessageLength != -1 && s.length() > fMaxMessageLength)
			s= s.substring(0, fMaxMessageLength)+"...";
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
		return getLoader().load(suiteClassName);
	}
	
	/**
	 * Clears the status message.
	 */
	protected void clearStatus() { // Belongs in the GUI TestRunner class
	}
	
	/**
	 * Returns the loader to be used.
	 */
	public TestSuiteLoader getLoader() {
		if (getPreference("loading").equals("true") && !inVAJava() && fLoading)
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
 	
 	public static String getPreference(String key) {
 		return fPreferences.getProperty(key);
 	}
 	
 	public static int getPreference(String key, int dflt) {
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

	/**
	 * Filters stack frames from internal JUnit classes
	 */
	public static String filterStack(String stack) {
		if (!getPreference("stackfilter").equals("true"))
			return stack;
			
		StringWriter sw= new StringWriter(500);
		PrintWriter pw= new PrintWriter(sw);
		StringReader sr= new StringReader(stack);
		BufferedReader br= new BufferedReader(sr);
		
		String line;
		try {	
			while ((line= br.readLine()) != null) {
				if (!filterLine(line))
					pw.println(line);
			}
		} catch (Exception IOException) {
			return stack; // return the stack unfiltered
		}
		return sw.toString();
	}
	
	static boolean filterLine(String line) {
		String[] patterns= new String[] {
			"junit.framework.TestCase",
			"junit.framework.TestResult",
			"junit.framework.TestSuite",
			"junit.framework.Assert.", // don't filter AssertionFailure
			"junit.swingui.TestRunner",
			"junit.awtui.TestRunner",
			"junit.textui.TestRunner",
			"java.lang.reflect.Method.invoke("
		};
		for (int i= 0; i < patterns.length; i++) {
			if (line.indexOf(patterns[i]) > 0)
				return true;
		}
		return false;
	}

 	{
 		fPreferences= new Properties();
 		//JDK 1.2 feature
 		//fPreferences.setProperty("loading", "true");
 		fPreferences.put("loading", "true");
 		fPreferences.put("stackfilter", "true");
  		readPreferences();
 		fMaxMessageLength= getPreference("maxmessage", fMaxMessageLength);
 	}
 	
}