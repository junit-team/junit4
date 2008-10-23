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
	
	@Test public void preferNewTests() {
		Request one= Request.method(TwoTests.class, "succeed");
		MaxCore max= new MaxCore();
		max.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= max.sort(two);
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, things.get(0));
		assertEquals(2, things.size());
	}
	
	// This covers a seemingly-unlikely case, where you had a test that failed on the
	// last run and you also introduced new tests. In such a case it pretty much doesn't matter
	// which order they run, you just want them both to be early in the sequence
	@Test public void preferNewTestsOverTestsThatFailed() {
		Request one= Request.method(TwoTests.class, "dontSucceed");
		MaxCore max= new MaxCore();
		max.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= max.sort(two);
		Description succeed= Description.createTestDescription(TwoTests.class, "succeed");
		assertEquals(succeed, things.get(0));
		assertEquals(2, things.size());
	}
	
	@Test public void preferRecentlyFailed() {
		Request request= Request.aClass(TwoTests.class);
		MaxCore max= new MaxCore();
		max.run(request);
		List<Description> tests= max.sort(request);
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, tests.get(0));
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
