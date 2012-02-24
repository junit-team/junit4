package org.junit.tests.validation;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.tests.validation.anotherpackage.Sub;

public class InaccessibleBaseClassTest {
	@Test(expected= InitializationError.class)
	public void inaccessibleBaseClassIsCaughtAtValidation() throws InitializationError {
		new BlockJUnit4ClassRunner(Sub.class);
	}
}
