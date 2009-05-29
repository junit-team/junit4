package org.junit.tests.assertion;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.either;
import static org.junit.matchers.JUnitMatchers.isOneOf;
import static org.junit.matchers.JUnitMatchers.matches;

import java.util.Arrays;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class BothTest {
	@DataPoint
	public static Matcher<? super Integer> IS_3= is(3);

	@DataPoint
	public static Matcher<? super Integer> IS_4= is(4);

	@DataPoint
	public static int THREE= 3;

	@Test
	public void bothPasses() {
		assertThat(3, both(any(Integer.class)).and(is(3)));
		assertThat("ab", both(containsString("a")).and(containsString("b")));
	}

	@Theory
	public void bothFails(int value, Matcher<Integer> first,
			Matcher<Integer> second) {
		assumeTrue(!(first.matches(value) && second.matches(value)));
		assertThat(value, not(both(first).and(second)));
	}

	@Theory
	public <T> void descriptionIsSensible(Matcher<T> first, Matcher<T> second) {
		Matcher<?> both= both(first).and(second);
		assertThat(both.toString(), containsString(first.toString()));
		assertThat(both.toString(), containsString(second.toString()));
	}

	@Test
	public void eitherPasses() {
		assertThat(3, either(sameInstance(3)).or(sameInstance(4)));
		assertThat(3, either(matches(is(String.class))).or(
				matches(is(Integer.class))));
		assertThat("a", either(sameInstance("a")).or(sameInstance("b")));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void isOneOfPasses() {
		assertThat(3, isOneOf(3, 4));
		assertThat(Arrays.asList("a"), isOneOf(Arrays.asList("a"), Arrays
				.asList("b")));
	}

	@Theory
	public <T> void threeAndsWork(Matcher<Integer> first,
			Matcher<Integer> second, Matcher<Integer> third, int value) {
		assumeTrue(first.matches(value) && second.matches(value)
				&& third.matches(value));
		assertThat(value, both(first).and(second).and(third));
	}

	@Theory
	public <T> void threeOrsWork(Matcher<Integer> first,
			Matcher<Integer> second, Matcher<Integer> third, int value) {
		assumeTrue(first.matches(value) || second.matches(value)
				|| third.matches(value));
		assertThat(value, either(first).or(second).or(third));
	}

	@Test
	public void superclassesAreOkInSecondPositionOnly() {
		assertThat("a", both(containsString("a")).and(is(String.class)));
	}
}
