package org.junit.experimental.theories.test.imposterization;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.experimental.theories.matchers.api.StringContains.containsString;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.experimental.imposterization.ThrownMatcher;
import org.junit.experimental.imposterization.ThrownMatcher.IncorrectThrownException;
import org.junit.experimental.theories.runner.api.Theories;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class ThrownMatcherTest {
	@Test
	public void assertThrownVerifiesExceptionThrown() throws Exception {
		ArithmeticException ae= new ArithmeticException();
		try {
			ThrownMatcher.assertThrownException(is(ae)).when(
					new ArrayList<String>()).get(0);
			fail("should have thrown exception");
		} catch (IncorrectThrownException e) {
			assertThat(e.getMessage(), containsString(ae.toString()));
		}
	}

	@Test
	public void assertThrownVerifiesExceptionThrownWithRequirements()
			throws Exception {
		ArithmeticException ae= new ArithmeticException();
		try {
			ThrownMatcher.assertThrownException(is(ae)).when(
					new ArrayList<String>()).get(0);
			fail("should have thrown exception");
		} catch (IncorrectThrownException e) {
			assertThat(e.getMessage(), containsString(ae.toString()));
		}
	}

	public static class ExceptionThrowing {
		public static Throwable EXCEPTION= new NullPointerException();

		public void liveDangerously() throws Throwable {
			throw EXCEPTION;
		}
	}

	@Test
	public void stackTraceOnAssertThrown() {
		try {
			ThrownMatcher.assertThrownException(is(new RuntimeException()))
					.when(new ExceptionThrowing()).liveDangerously();
			fail("should have thrown exception");
		} catch (Throwable e) {
			assertThat(e.getCause(), is(ExceptionThrowing.EXCEPTION));
		}
	}
}
