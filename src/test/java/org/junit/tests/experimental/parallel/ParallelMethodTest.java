package org.junit.tests.experimental.parallel;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
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
	
	@Test/*(timeout=1500)*/ public void testsRunInParallel() {
		long start= System.currentTimeMillis();
		Result result= JUnitCore.runClasses(ParallelComputer.methods(), Example.class);
		assertTrue(result.wasSuccessful());
		long end= System.currentTimeMillis();
		assertThat(end - start, greaterThanOrEquals(1000));
	}

	private Matcher<Long> greaterThanOrEquals(final long l) {
		return new TypeSafeMatcher<Long>() {
			@Override
			public boolean matchesSafely(Long item) {
				return item >= l;
			}

			public void describeTo(Description description) {
				description.appendText("greater than or equal" + l);
			}
		};
	}
}
