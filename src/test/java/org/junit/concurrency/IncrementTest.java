package org.junit.concurrency;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ConcurrencyJunitRunner.class)
public class IncrementTest {
	
	private static final int TIMES = 1000;
	private static final int THREADS = 100;
	
	private static double standardIncrement = 1;
	
	private static volatile double volatileIncrement = 1;
	
	private static double synchronizedIncrement = 1;
	
	@Ignore("Works not any time.. Like expected.")
	@Test
	@Concurrency(times = TIMES, parallelThreads = THREADS, expectMinimumSuccessRuns = 1, expectMaximumSuccessRuns = 1, expectAtLeast = AssertionError.class)
	public void testStandardIncrement() {
		standardIncrement++;
		Assert.assertEquals(TIMES, standardIncrement, 0);
	}
	
	@Ignore("Works not any time.. Unexpected!?")
	@Test
	@Concurrency(times = TIMES, parallelThreads = THREADS, expectMinimumSuccessRuns = 1, expectMaximumSuccessRuns = 1, expectAtLeast = AssertionError.class)
	public void testVolatileIncrement() {
		volatileIncrement++;
		Assert.assertEquals(TIMES, volatileIncrement, 0);
	}
	
	@Test
	@Concurrency(times = TIMES, parallelThreads = THREADS, expectMinimumSuccessRuns = 1, expectMaximumSuccessRuns = 1, expectAtLeast = AssertionError.class)
	public void testSynchronizedIncrement() {
		synchronized (this) {
			System.out.println(this);
			synchronizedIncrement++;
			Assert.assertEquals(TIMES, synchronizedIncrement, 0);
		}
	}
}
