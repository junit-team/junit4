package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.internal.runners.TestClass;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;

public class WithDataPointFields {
	@RunWith(Theories.class)
	public static class HasATheory {
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
	public static class HasATwoParameterTheory {
		public static int ONE= 1;

		@Theory
		public void everythingIsZero(int x, int y) {
			assertThat(x, is(y));
		}
	}

	@Test
	public void canRunTwoParameterTheories() throws Exception {
		assertThat(testResult(HasATwoParameterTheory.class), ResultMatchers
				.isSuccessful());
	}

	@RunWith(Theories.class)
	public static class DoesntUseParams {
		public static int ONE= 1;

		@Theory
		public void everythingIsZero(int x, int y) {
			assertThat(2, is(3));
		}
	}

	@Test
	public void reportBadParams() throws Exception {
		assertThat(testResult(DoesntUseParams.class),
				hasSingleFailureContaining("everythingIsZero(1, 1)"));
	}

	@RunWith(Theories.class)
	public static class NullsOK {
		public static String NULL= null;

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

	// TODO: (Jul 20, 2007 1:58:18 PM) too complex

	@RunWith(Theories.class)
	public static class BeforeAndAfterEachTime {
		public static int befores= 0;

		@DataPoint
		public static String A= "A";

		@DataPoint
		public static String B= "B";

		@Before
		public void incrementBefore() {
			befores++;
		}

		@Theory
		public void stringsAreOK(String string) {
		}
	}

	@Test
	public void beforeIsCalledOnEachParameterSet() {
		BeforeAndAfterEachTime.befores= 0;
		JUnitCore.runClasses(BeforeAndAfterEachTime.class);
		assertThat(BeforeAndAfterEachTime.befores, is(2));
	}

	@RunWith(Theories.class)
	public static class NewObjectEachTime {
		@DataPoint
		public static String A= "A";

		@DataPoint
		public static String B= "B";

		private List<String> list= new ArrayList<String>();

		@Theory
		public void addToEmptyList(String string) {
			list.add(string);
			assertThat(list.size(), is(1));
		}
	}

	@Test
	public void newObjectEachTime() {
		PrintableResult result= testResult(NewObjectEachTime.class);
		assertThat(result, isSuccessful());
	}
}