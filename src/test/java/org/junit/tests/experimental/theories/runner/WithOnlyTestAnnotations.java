package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

public class WithOnlyTestAnnotations {
	@RunWith(Theories.class)
	public static class HonorExpectedException {
		@Test(expected= NullPointerException.class)
		public void shouldThrow() {

		}
	}

	@Test
	public void honorExpected() throws Exception {
		assertThat(testResult(HonorExpectedException.class).getFailures()
				.size(), is(1));
	}
	
	@RunWith(Theories.class)
	public static class HonorExpectedExceptionPasses {
		@Test(expected= NullPointerException.class)
		public void shouldThrow() {
			throw new NullPointerException();
		}
	}

	@Test
	public void honorExpectedPassing() throws Exception {
		assertThat(testResult(HonorExpectedExceptionPasses.class), isSuccessful());
	}

	@RunWith(Theories.class)
	public static class HonorTimeout {
		@Test(timeout= 5)
		public void shouldStop() {
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

				}
			}
		}
	}

	@Test
	public void honorTimeout() throws Exception {
		assertThat(testResult(HonorTimeout.class), failureCountIs(1));
	}
}