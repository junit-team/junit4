package org.junit.experimental.test.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.imposterization.FunctionPointer;
import org.junit.experimental.theories.methods.api.DataPoint;
import org.junit.experimental.theories.methods.api.ParameterSignature;
import org.junit.experimental.theories.methods.api.Theory;
import org.junit.experimental.theories.runner.TheoryContainerReference;
import org.junit.experimental.theories.runner.api.Theories;
import org.junit.runner.RunWith;
import static org.junit.matchers.StringContains.containsString;

public class TheoryContainerReferenceTest {
	private FunctionPointer method = new FunctionPointer();
	private final HasDataPointMethod test = new HasDataPointMethod();

	@RunWith(Theories.class)
	public static class HasDataPointMethod {
		@DataPoint public int oneHundred() {
			return 100;
		}

		public Date notADataPoint() {
			return new Date();
		}

		@Theory public void allIntsOk(int x) {

		}

		@Theory public void onlyStringsOk(String s) {

		}

		@Theory public void onlyDatesOk(Date d) {

		}
	}

	@Test public void pickUpDataPointMethods() throws SecurityException,
			InstantiationException, IllegalAccessException {
		method.calls(test).allIntsOk(0);
		assertThat(potentialValues().toString(), containsString("100"));
	}

	@Test public void ignoreDataPointMethodsWithWrongTypes()
			throws SecurityException, InstantiationException,
			IllegalAccessException {
		method.calls(test).onlyStringsOk(null);
		assertThat(potentialValues().toString(), not(containsString("100")));
	}

	@Test public void ignoreDataPointMethodsWithoutAnnotation()
			throws SecurityException, InstantiationException,
			IllegalAccessException {
		method.calls(test).onlyDatesOk(null);
		assertThat(potentialValues().size(), is(0));
	}

	private List<?> potentialValues() throws InstantiationException,
			IllegalAccessException {
		return new TheoryContainerReference(test)
				.getPotentialValues(ParameterSignature.signatures(
						method.getMethod()).get(0));
	}
}
