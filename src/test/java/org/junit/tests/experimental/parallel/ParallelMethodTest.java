package org.junit.tests.experimental.parallel;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ParallelMethodTest {
	public static class Example {
		@Test public void one() throws InterruptedException {
			Thread.sleep(1000);
		}
		@Test public void two() throws InterruptedException {
			Thread.sleep(1000);
		}
	}
	
	@Test public void testsRunInParallel() {
		long start= System.currentTimeMillis();
		Result result= JUnitCore.runClasses(ParallelComputer.methods(),
				Example.class);
		assertTrue(result.wasSuccessful());
		long end= System.currentTimeMillis();
		assertThat(end - start, betweenInclusive(1000, 1900));
	}

	private Matcher<Long> betweenInclusive(final long min, final long max) {
		return new TypeSafeMatcher<Long>() {
			@Override
			public boolean matchesSafely(Long item) {
				return item >= min && item <= max;
			}

			public void describeTo(Description description) {
				description.appendText("between " + min + " and " + max);
			}
		};
	}
}
