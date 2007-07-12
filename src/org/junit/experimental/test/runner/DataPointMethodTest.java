package org.junit.experimental.test.runner;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.Each.each;

import java.util.List;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.theories.methods.api.DataPoint;
import org.junit.experimental.theories.methods.api.Theory;
import org.junit.experimental.theories.runner.api.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

public class DataPointMethodTest {
	@RunWith(Theories.class)
	public static class HasDataPointMethod {
		@DataPoint
		public int oneHundred() {
			return 100;
		}

		@Theory
		public void allIntsOk(int x) {

		}
	}
	
	@RunWith(Theories.class)
	public static class HasUglyDataPointMethod {
		@DataPoint
		public int oneHundred() {
			return 100;
		}

		@DataPoint
		public int oneUglyHundred() {
			throw new RuntimeException();
		}

		@Theory
		public void allIntsOk(int x) {

		}
	}

	@Test
	public void pickUpDataPointMethods() {
		assertThat(failures(HasDataPointMethod.class), empty());
	}

	@Test
	public void ignoreExceptionsFromDataPointMethods() {
		assertThat(failures(HasUglyDataPointMethod.class), empty());
	}

	private List<Failure> failures(Class<?> type) {
		return JUnitCore.runClasses(type).getFailures();
	}

	private Matcher<Iterable<Failure>> empty() {
		Matcher<Failure> nullValue= nullValue();
		return each(nullValue);
	}
}
