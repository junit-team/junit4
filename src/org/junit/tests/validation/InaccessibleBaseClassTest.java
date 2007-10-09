package org.junit.tests.validation;

import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.internal.runners.model.ErrorList;
import org.junit.internal.runners.model.TestClass;
import org.junit.tests.validation.anotherpackage.Sub;

public class InaccessibleBaseClassTest {	
	@Test
	public void inaccessibleBaseClassIsCaughtAtValidation() {
		TestClass testClass= new TestClass(Sub.class);
		ErrorList errors= new ErrorList();
		testClass.validateMethodsForDefaultRunner(errors);
		assertFalse(errors.isEmpty());
	}
}
