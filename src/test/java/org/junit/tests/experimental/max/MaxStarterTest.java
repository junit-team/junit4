package org.junit.tests.experimental.max;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.max.CouldNotReadCoreException;
import org.junit.experimental.max.MaxCore;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.Request;


public class MaxStarterTest {
	
	public static class TwoTests {
		@Test public void succeed() {}
		@Test public void dontSucceed() { fail(); }
	}
	
	@Test public void twoTestsNotRunComeBackInRandomOrder() {
		Request request= Request.aClass(TwoTests.class);
		MaxCore max= MaxCore.createFresh();
		List<Description> things= max.sortedLeavesForTest(request);
		Description succeed= Description.createTestDescription(TwoTests.class, "succeed");
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertTrue(things.contains(succeed));
		assertTrue(things.contains(dontSucceed));
		assertEquals(2, things.size());
	}
	
	@Test public void preferNewTests() {
		Request one= Request.method(TwoTests.class, "succeed");
		MaxCore max= MaxCore.createFresh();
		max.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= max.sortedLeavesForTest(two);
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, things.get(0));
		assertEquals(2, things.size());
	}
	
	// This covers a seemingly-unlikely case, where you had a test that failed on the
	// last run and you also introduced new tests. In such a case it pretty much doesn't matter
	// which order they run, you just want them both to be early in the sequence
	@Test public void preferNewTestsOverTestsThatFailed() {
		Request one= Request.method(TwoTests.class, "dontSucceed");
		MaxCore max= MaxCore.createFresh();
		max.run(one);
		Request two= Request.aClass(TwoTests.class);
		List<Description> things= max.sortedLeavesForTest(two);
		Description succeed= Description.createTestDescription(TwoTests.class, "succeed");
		assertEquals(succeed, things.get(0));
		assertEquals(2, things.size());
	}
	
	@Test public void preferRecentlyFailed() {
		Request request= Request.aClass(TwoTests.class);
		MaxCore max= MaxCore.createFresh();
		max.run(request);
		List<Description> tests= max.sortedLeavesForTest(request);
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, tests.get(0));
	}
	
	@Test public void sortTestsInMultipleClasses() {
		Request request= Request.classes(Computer.serial(), TwoTests.class, TwoTests.class);
		MaxCore max= MaxCore.createFresh();
		max.run(request);
		List<Description> tests= max.sortedLeavesForTest(request);
		Description dontSucceed= Description.createTestDescription(TwoTests.class, "dontSucceed");
		assertEquals(dontSucceed, tests.get(0));
		assertEquals(dontSucceed, tests.get(1));
	}	
	
	public static class TwoUnEqualTests {
		@Test public void slow() throws InterruptedException { Thread.sleep(100); }
		@Test public void fast() throws InterruptedException { Thread.sleep(50); }
	}
	
	@Test public void preferFast() {
		Request request= Request.aClass(TwoUnEqualTests.class);
		MaxCore max= MaxCore.createFresh();
		max.run(request);
		Description thing= max.sortedLeavesForTest(request).get(1);
		assertEquals(Description.createTestDescription(TwoUnEqualTests.class, "slow"), thing);
	}
	
	@Test public void remember() throws CouldNotReadCoreException {
		Request request= Request.aClass(TwoUnEqualTests.class);
		MaxCore original= MaxCore.forFolder("folder");
		original.run(request);
		MaxCore reincarnation= MaxCore.forFolder("folder");
		Description thing= reincarnation.sortedLeavesForTest(request).get(1);
		assertEquals(Description.createTestDescription(TwoUnEqualTests.class, "slow"), thing);	
	}
}
