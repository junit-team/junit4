package org.junit.experimental.test.theories;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ParameterSignatureTest {
	@DataPoint public static Method getType() throws SecurityException, NoSuchMethodException {
		return ParameterSignatureTest.class.getMethod("getType", Method.class, int.class);
	}

	@DataPoint public static int ZERO = 0;
	@DataPoint public static int ONE = 1;
	
	@Theory
	public void getType(Method method, int index) {
		assertEquals(method.getParameterTypes()[index], ParameterSignature
				.signatures(method).get(index).getType());
	}
}
