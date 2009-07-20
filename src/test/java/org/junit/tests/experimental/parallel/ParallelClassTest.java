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
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


public class ParallelClassTest {

	public static class Example1 {
		@Test public void one() throws InterruptedException {
			Thread.sleep(1000);
		}
	}
	public static class Example2 {
		@Test public void one() throws InterruptedException {
			Thread.sleep(1000);
		}
	}
	
	@RunWith(Suite.class)
	@SuiteClasses({Example1.class, Example2.class})
	public static class ExampleSuite {}
	
	@Test(timeout=1500) public void testsRunInParallel() {
		long start= System.currentTimeMillis();
		Result result= JUnitCore.runClasses(ParallelComputer.classes(), Example1.class, Example2.class);
		assertTrue(result.wasSuccessful());
		long end= System.currentTimeMillis();
		assertThat(end - start, greaterThan(999)); // Overhead could be less than half a millisecond
	}

	private Matcher<Long> greaterThan(final long l) {
		return new TypeSafeMatcher<Long>() {
			@Override
			public boolean matchesSafely(Long item) {
				return item > l;
			}

			public void describeTo(Description description) {
				description.appendText("greater than " + l);
			}
		};
	}
}
