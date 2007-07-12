package org.junit.experimental.test.imposterization;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.experimental.imposterization.AssumePassing.assumePasses;

import java.util.List;

import org.junit.Test;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.experimental.imposterization.AssumePassing;
import org.junit.experimental.theories.runner.api.Theories;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

@RunWith(Theories.class)
public class AssumePassingTest {
	public static class OnlyIfPassingButDoesnt {
		@Test
		public void failing() {
			fail();
		}

		@SuppressWarnings("deprecation")
		@Test
		public void willIgnore() {
			((OnlyIfPassingButDoesnt) AssumePassing.assumePasses(this
					.getClass())).failing();
			fail();
		}
	}

	@Test
	public void onlyIfPassingButDoesnt() {
		assertThat(onlyIfPassingFailures().size(), is(1));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void removedParameterizedFailureWhenZeroParams() {
		assertThat(onlyIfPassingFailures().get(0).getException(), is(AssertionError.class));
	}

	private List<Failure> onlyIfPassingFailures() {
		return JUnitCore.runClasses(OnlyIfPassingButDoesnt.class).getFailures();
	}

	@RunWith(Theories.class)
	public static class OnlyIfPassesAndDoes {
		@Test
		public void passing() {
		}

		@SuppressWarnings("deprecation")
		@Test
		public void wontIgnore() {
			assumePasses(OnlyIfPassesAndDoes.class).passing();
			fail();
		}
	}

	@Test
	public void onlyIfPassesAndDoes() {
		assertThat(JUnitCore.runClasses(OnlyIfPassesAndDoes.class)
				.getFailures().size(), is(1));
	}

	@RunWith(Theories.class)
	public static class OnlyIfPassesWithInvalidTheory {
		@Test
		public void throwsInvalidTheory() {
			throw new AssumptionViolatedException(null, is("a"));
		}

		@SuppressWarnings("deprecation")
		@Test
		public void wontIgnore() {
			assumePasses(OnlyIfPassesWithInvalidTheory.class)
					.throwsInvalidTheory();
		}
	}

	@Test
	public void onlyIfPassesWithInvalidTheory() {
		assertThat(JUnitCore.runClasses(OnlyIfPassesWithInvalidTheory.class)
				.getIgnoreCount(), is(0));
	}
}
