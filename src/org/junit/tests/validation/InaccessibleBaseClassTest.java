package org.junit.tests.validation;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.internal.runners.model.TestClass;
import org.junit.tests.validation.anotherpackage.Sub;

public class InaccessibleBaseClassTest {	
	@Test
	public void inaccessibleBaseClassIsCaughtAtValidation() {
		TestClass testClass= new TestClass(Sub.class);
		List<Throwable> errors= new ArrayList<Throwable>();
		testClass.validateMethodsForDefaultRunner(errors);
		assertFalse(errors.isEmpty());
	}
}
