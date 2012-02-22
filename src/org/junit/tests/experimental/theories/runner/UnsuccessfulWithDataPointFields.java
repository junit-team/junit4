package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasFailureContaining;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.matchers.JUnitMatchers.both;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.junit.runners.model.TestClass;

public class UnsuccessfulWithDataPointFields {
	@RunWith(Theories.class)
	public static class HasATheory {
		@DataPoint
		public static int ONE= 1;

		@Theory
		public void everythingIsZero(int x) {
			assertThat(x, is(0));
		}
	}

	@Test
	public void theoryClassMethodsShowUp() throws Exception {
		assertThat(new Theories(HasATheory.class).getDescription()
				.getChildren().size(), is(1));
	}

	@Test
	public void theoryAnnotationsAreRetained() throws Exception {
		assertThat(new TestClass(HasATheory.class).getAnnotatedMethods(
				Theory.class).size(), is(1));
	}

	@Test
	public void canRunTheories() throws Exception {
		assertThat(testResult(HasATheory.class),
				hasSingleFailureContaining("Expected"));
	}

	@RunWith(Theories.class)
	public static class DoesntUseParams {
		@DataPoint
		public static int ONE= 1;

		@Theory
		public void everythingIsZero(int x, int y) {
			assertThat(2, is(3));
		}
	}

	@Test
	public void reportBadParams() throws Exception {
		assertThat(testResult(DoesntUseParams.class),
				hasSingleFailureContaining("everythingIsZero(ONE, ONE)"));
	}

	@RunWith(Theories.class)
	public static class NullsOK {
		@DataPoint
		public static String NULL= null;

		@DataPoint
		public static String A= "A";

		@Theory
		public void everythingIsA(String a) {
			assertThat(a, is("A"));
		}
	}

	@Test
	public void nullsUsedUnlessProhibited() throws Exception {
		assertThat(testResult(NullsOK.class),
				hasSingleFailureContaining("null"));
	}

	@RunWith(Theories.class)
	public static class DataPointsMustBeStatic {
		@DataPoint
		int THREE= 3;

		@DataPoint
		int FOUR= 3;

		@Theory
		public void numbers(int x) {

		}
	}

	@Test
	public void dataPointsMustBeStatic() {
		assertThat(
				testResult(DataPointsMustBeStatic.class),
				both(failureCountIs(2))
						.and(
								hasFailureContaining("DataPoint field THREE must be static"))
						.and(
								hasFailureContaining("DataPoint field FOUR must be static")));
	}

	@RunWith(Theories.class)
	public static class TheoriesMustBePublic {
		@DataPoint
		public static int THREE= 3;

		@Theory
		void numbers(int x) {

		}
	}

	@Test
	public void theoriesMustBePublic() {
		assertThat(
				testResult(TheoriesMustBePublic.class),
				hasSingleFailureContaining("public"));
	}
}
