package junit.runner;

/**
 * A TestSuite loader that can reload classes.
 */
public class ReloadingTestSuiteLoader implements TestSuiteLoader {
	public Class load(String suiteClassName) throws ClassNotFoundException {
		TestCaseClassLoader loader= new TestCaseClassLoader();
		return loader.loadClass(suiteClassName, true);
	}
	public Class reload(Class aClass) throws ClassNotFoundException {
		TestCaseClassLoader loader= new TestCaseClassLoader();
		return loader.loadClass(aClass.getName(), true);
	}
}