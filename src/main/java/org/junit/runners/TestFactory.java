package org.junit.runners;

import java.util.List;

import org.junit.runners.model.FrameworkTest;
import org.junit.runners.model.TestClass;

public interface TestFactory {

	/**
	 * Discovers test methods.
	 * 
	 * @param testClass
	 * @param errors
	 *            If not <code>null</code>, then validation is performed and
	 *            errors are added to the list.
	 * @return
	 */
	List<FrameworkTest> computeTestMethods(TestClass testClass, List<Throwable> errors);
}
