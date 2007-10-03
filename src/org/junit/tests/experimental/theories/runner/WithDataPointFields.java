package org.junit.tests.experimental.theories.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.hasSingleFailureContaining;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
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
	public static class BeforeAndAfterOnSameInstance {
		@DataPoint
		public static String A= "A";

		private int befores= 0;

		@Before
		public void incrementBefore() {
			befores++;
		}

		@Theory
		public void stringsAreOK(String string) {
			assertTrue(befores == 1);
		}
	}

	@Test
	public void beforeIsCalledOnSameInstance() {
		assertThat(testResult(BeforeAndAfterOnSameInstance.class),
				isSuccessful());
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

	@RunWith(Theories.class)
	public static class PositiveInts {
		@DataPoint
		public static final int ONE= 1;

		private int x;

		public PositiveInts(int x) {
			assumeTrue(x > 0);
			this.x= x;
		}

		@Theory
		public void haveAPostiveSquare() {
			assertTrue(x * x > 0);
		}
	}

	@Ignore("until construction is handled in TestMethod")
	@Test
	public void honorConstructorParameters() {
		assertThat(testResult(PositiveInts.class), isSuccessful());
	}
}