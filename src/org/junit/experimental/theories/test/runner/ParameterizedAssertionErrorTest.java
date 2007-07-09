package org.junit.experimental.theories.test.runner;

import static org.junit.experimental.theories.matchers.api.StringContains.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import org.junit.experimental.theories.methods.api.Theory;
import org.junit.experimental.theories.runner.ParameterizedAssertionError;
import org.junit.experimental.theories.runner.api.Theories;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class ParameterizedAssertionErrorTest {
	public static final String METHOD_NAME= "methodName";

	public static final NullPointerException NULL_POINTER_EXCEPTION= new NullPointerException();

	public static Object[] NO_OBJECTS= new Object[0];

	public static ParameterizedAssertionError A= new ParameterizedAssertionError(
			NULL_POINTER_EXCEPTION, METHOD_NAME);

	public static ParameterizedAssertionError B= new ParameterizedAssertionError(
			NULL_POINTER_EXCEPTION, METHOD_NAME);

	public static ParameterizedAssertionError B2= new ParameterizedAssertionError(
			NULL_POINTER_EXCEPTION, "methodName2");

	@Theory
	public void equalParameterizedAssertionErrorsHaveSameToString(
			ParameterizedAssertionError a, ParameterizedAssertionError b) {
		assumeThat(a, is(b));
		assertThat(a.toString(), is(b.toString()));
	}

	@Theory
	public void differentParameterizedAssertionErrorsHaveDifferentToStrings(
			ParameterizedAssertionError a, ParameterizedAssertionError b) {
		assumeThat(a, not(b));
		assertThat(a.toString(), not(b.toString()));
	}

	@Theory
	public void equalsReturnsTrue(Throwable targetException, String methodName,
			Object[] params) {
		assertThat(new ParameterizedAssertionError(targetException, methodName,
				params), is(new ParameterizedAssertionError(targetException,
				methodName, params)));
	}

	@SuppressWarnings("unchecked")
	@Theory(nullsAccepted= false)
	public void buildParameterizedAssertionError(String methodName, String param) {
		assertThat(new ParameterizedAssertionError(new RuntimeException(),
				methodName, param).toString(), containsString(methodName));
	}
}
