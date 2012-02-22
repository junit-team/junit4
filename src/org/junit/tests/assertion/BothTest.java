package org.junit.tests.assertion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.containsString;
import static org.junit.matchers.JUnitMatchers.either;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class BothTest {
	@DataPoint
	public static Matcher<Integer> IS_3= is(3);

	@DataPoint
	public static Matcher<Integer> IS_4= is(4);

	@DataPoint
	public static int THREE= 3;

	@Test
	public void bothPasses() {
		assertThat(3, both(is(Integer.class)).and(is(3)));
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
		assertThat(3, either(is(3)).or(is(4)));
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
	
	@Test public void subclassesAreOkInSecondPositionOnly() {
		assertThat(3, both(is(Integer.class)).and(is(3)));
	}
}
