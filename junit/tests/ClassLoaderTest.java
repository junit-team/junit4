package junit.tests;

/**
 * Test class used in TestTestCaseClassLoader
 */
import junit.framework.*;
import junit.runner.*;

public class ClassLoaderTest extends Assert {
	public ClassLoaderTest() {
	}
	public void verify() {
		verifyApplicationClassLoadedByTestLoader();
		verifySystemClassNotLoadedByTestLoader();
	}
	private boolean isTestCaseClassLoader(ClassLoader cl) {
		return (cl != null && cl.getClass().getName().equals(junit.runner.TestCaseClassLoader.class.getName()));
	}
	private void verifyApplicationClassLoadedByTestLoader() {
		assert(isTestCaseClassLoader(getClass().getClassLoader()));
	} 
	private void verifySystemClassNotLoadedByTestLoader() {
		assert(!isTestCaseClassLoader(Object.class.getClassLoader()));
		assert(!isTestCaseClassLoader(TestCase.class.getClassLoader()));
	}
}