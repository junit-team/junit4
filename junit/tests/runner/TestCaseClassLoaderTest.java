package junit.tests.runner;

import java.lang.reflect.*;
import junit.framework.*;
import junit.runner.*;
import java.net.URL;

/**
 * A TestCase for testing the TestCaseClassLoader
 *
 */
public class TestCaseClassLoaderTest extends TestCase {

	public void testClassLoading() throws Exception {
		TestCaseClassLoader loader= new TestCaseClassLoader();
		Class loadedClass= loader.loadClass("junit.tests.runner.ClassLoaderTest", true);
		Object o= loadedClass.newInstance();
		//
		// Invoke the assertClassLoaders method via reflection.
		// We use reflection since the class is loaded by
		// another class loader and we can't do a successfull downcast to
		// ClassLoaderTestCase.
		//
		Method method= loadedClass.getDeclaredMethod("verify", new Class[0]);
		method.invoke(o, new Class[0]);
	}

	public void testJarClassLoading() throws Exception {
		URL url= getClass().getResource("test.jar");
		assertNotNull("Cannot find test.jar", url);
		String path= url.getFile();
		TestCaseClassLoader loader= new TestCaseClassLoader(path);
		Class loadedClass= loader.loadClass("junit.tests.runner.LoadedFromJar", true);
		Object o= loadedClass.newInstance();
		//
		// Invoke the assertClassLoaders method via reflection.
		// We use reflection since the class is loaded by
		// another class loader and we can't do a successfull downcast to
		// ClassLoaderTestCase.
		//
		Method method= loadedClass.getDeclaredMethod("verify", new Class[0]);
		method.invoke(o, new Class[0]);
	}
}