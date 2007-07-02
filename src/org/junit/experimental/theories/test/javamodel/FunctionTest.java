package org.junit.experimental.theories.test.javamodel;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.imposterization.FunctionPointer;
import org.junit.experimental.theories.methods.api.TestedOn;

public class FunctionTest {
	public static class HasAnnotation {
		public void something(@TestedOn(ints= { 3 })
		int x) {
		}
	}

	FunctionPointer function= FunctionPointer.pointer();

	@Before
	public void functionPoints() {
		function.calls(new HasAnnotation()).something(4);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSupplierAnnotation() {
		assertThat(function.signatures().get(0).getSupplierAnnotation(),
				is(TestedOn.class));
	}

	@Test
	public void thrownReturnsNullIfNormalReturn() {
		assertThat(function.exceptionThrown(4), nullValue());
	}
}
