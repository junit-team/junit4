package junit.tests.runner;

/**
 * Test class used in TestTestCaseClassLoader
 */
import junit.framework.Assert;
import junit.framework.TestCase;

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
		assertTrue(isTestCaseClassLoader(getClass().getClassLoader()));
	} 
	private void verifySystemClassNotLoadedByTestLoader() {
		assertTrue(!isTestCaseClassLoader(Object.class.getClassLoader()));
		assertTrue(!isTestCaseClassLoader(TestCase.class.getClassLoader()));
	}
}