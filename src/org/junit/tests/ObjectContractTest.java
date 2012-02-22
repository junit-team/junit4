package org.junit.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.Test.None;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;

@RunWith(Theories.class)
public class ObjectContractTest {
	@DataPoints
	public static Object[] objects= { new FrameworkMethod(toStringMethod()),
			new FrameworkMethod(toStringMethod()), 3, null };

	@Theory
	@Test(expected= None.class)
	public void equalsThrowsNoException(Object a, Object b) {
		assumeNotNull(a);
		a.equals(b);
	}

	@Theory
	public void equalsMeansEqualHashCodes(Object a, Object b) {
		assumeNotNull(a, b);
		assumeThat(a, is(b));
		assertThat(a.hashCode(), is(b.hashCode()));
	}

	private static Method toStringMethod() {
		try {
			return Object.class.getMethod("toString");
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return null;
	}
}
