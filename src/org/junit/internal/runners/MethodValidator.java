package org.junit.internal.runners;

import java.util.ArrayList;
import java.util.List;

import org.junit.internal.runners.model.TestClass;

public class MethodValidator {

	public final List<Throwable> fErrors= new ArrayList<Throwable>();

	public TestClass fTestClass;

	public MethodValidator(TestClass testClass) {
		fTestClass = testClass;
	}

	public void assertValid() throws InitializationError {
		if (!fErrors.isEmpty())
			throw new InitializationError(fErrors);
	}
}
