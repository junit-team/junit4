package junit.tests.runner;

/**
 * Test class used in TestTestCaseClassLoader
 */
import junit.framework.Assert;

public class LoadedFromJar extends Assert {
	public void verify() {
		verifyApplicationClassLoadedByTestLoader();
	}
	private boolean isTestCaseClassLoader(ClassLoader cl) {
		return (cl != null && cl.getClass().getName().equals(junit.runner.TestCaseClassLoader.class.getName()));
	}
	private void verifyApplicationClassLoadedByTestLoader() {
		assertTrue(isTestCaseClassLoader(getClass().getClassLoader()));
	} 
}