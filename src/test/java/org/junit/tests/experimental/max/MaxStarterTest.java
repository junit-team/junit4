package org.junit.tests.experimental.max;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Request;


public class MaxStarterTest {
	
	public static class TwoTests {
		@Test public void succeed() {}
		@Test public void dontSucceed() { fail(); }
	}
	
	@Test public void twoTestsNotRun() {
		Request request= Request.aClass(TwoTests.class);
		MaxCore max= new MaxCore();
		List<Description> things= max.sort(request);
		Description succeed= Description.createTestDescription(TwoTests.class, "succeed");
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertTrue(things.contains(succeed));
		assertTrue(things.contains(dontSucceed));
		assertEquals(2, things.size());
	}
	
	@Test public void testsNotYetRunHavePriority() {
		Request one= Request.method(TwoTests.class, "succeed");
		MaxCore max= new MaxCore();
		max.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= max.sort(two);
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, things.get(0));
		assertEquals(2, things.size());
	}
	
	@Test public void newTestsHavePriorityOverTestsThatFailed() {
	//TODO work this out later
		Request one= Request.method(TwoTests.class, "dontSucceed");
		MaxCore max= new MaxCore();
		max.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= max.sort(two);
		Description succeed= Description.createTestDescription(TwoTests.class, "succeed");
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, things.get(0));
		assertEquals(2, things.size());
	}
	
	@Test public void preferRecentlyFailed() {
		Request request= Request.aClass(TwoTests.class);
		MaxCore max= new MaxCore();
		max.run(request);
		Odds thing= max.getSpreads(request).get(1);
		assertEquals(0.0, thing.getCertainty(), 0.001); // TODO not right yet
		assertEquals(Description.createTestDescription(TwoTests.class, "succeed"), thing.getDescription());
	}
	
	public static class TwoUnEqualTests {
		@Test public void slow() throws InterruptedException { Thread.sleep(100); }
		@Test public void fast() throws InterruptedException { Thread.sleep(50); }
	}
	
	@Test public void preferFast() {
		Request request= Request.aClass(TwoUnEqualTests.class);
		MaxCore max= new MaxCore();
		max.run(request);
		Description thing= max.sort(request).get(1);
		assertEquals(Description.createTestDescription(TwoUnEqualTests.class, "slow"), thing);
	}
}
