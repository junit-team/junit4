package org.junit.experimental.test.javamodel;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.matchers.StringContains.containsString;

import java.lang.reflect.Method;

import org.junit.experimental.theories.methods.api.Theory;
import org.junit.experimental.theories.runner.ConcreteFunction;
import org.junit.experimental.theories.runner.api.Theories;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ConcreteFunctionTest {
	public static Method TO_STRING;

	public static Method WAIT;

	static {
		try {
			TO_STRING= Object.class.getMethod("toString");
			WAIT= Object.class.getMethod("wait");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ConcreteFunction zeroToString1= new ConcreteFunction(0,
			TO_STRING);

	public static ConcreteFunction zeroToString2= new ConcreteFunction(0,
			TO_STRING);

	public static ConcreteFunction oneToString1= new ConcreteFunction(1,
			TO_STRING);

	public static ConcreteFunction oneFinalize= new ConcreteFunction(1, WAIT);

	public static String ROB_KUTNER= "Rob Kutner";

	@Theory
	public void unequalToStringsMeansFunctionsUnequal(ConcreteFunction a,
			ConcreteFunction b) {
		assumeThat(a.toString(), not(b.toString()));
		assertThat(a, not(b));
	}

	@Theory
	public void unequalMethodsMeansUnequalFunctions(Method m1, Method m2,
			Object o) {
		assumeThat(m1, not(m2));
		assertThat(new ConcreteFunction(o, m1),
				not(new ConcreteFunction(o, m2)));
	}

	@Theory
	public void unequalFunctionsMeanUnequalToStrings(ConcreteFunction a,
			ConcreteFunction b) {
		assumeThat(a, not(b));
		assertThat(a.toString(), not(b.toString()));
	}

	@SuppressWarnings("unchecked")
	@Theory
	public void throwsUsefulErrorWhenParameterNumberWrong(String string)
			throws Throwable {
		try {
			new ConcreteFunction(this, TO_STRING).invoke(string);
		} catch (Exception e) {
			assertThat(e.toString(), allOf(containsString(string),
					containsString(TO_STRING.toString())));
		}
	}
}
