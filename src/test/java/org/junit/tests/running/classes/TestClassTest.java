package org.junit.tests.running.classes;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.runners.model.TestClass;

public class TestClassTest {
	public static class TwoConstructors {
		public TwoConstructors() {}
		public TwoConstructors(int x) {}
	}
	
	@Test(expected=IllegalArgumentException.class) public void complainIfMultipleConstructors() {
		new TestClass(TwoConstructors.class);
	}
	
	public static class ManyMethods {
		@Test public void a() {}
		@Before public void b() {}
		@Ignore @Test public void c() {}
		@Ignore @After public void d() {}
		public void e() {}
		@BeforeClass public void f() {}
		public void g() {}
		@AfterClass public void h() {}
		@Test public void i() {}
		@Test public void j() {}
	}
	
	// Profiling a JUnit 4.4 suite shows that getAnnotatedMethods accounts for at least 13% of running time
	// (all running time, including user test code!)
	@Test(timeout=5) public void snappyRetrievalOfAnnotatedMethods() {
		TestClass testClass= new TestClass(ManyMethods.class);
		for (int i= 0; i < 100; i++) {
			testClass.getAnnotatedMethods(Test.class);
			testClass.getAnnotatedMethods(Before.class);
			testClass.getAnnotatedMethods(After.class);
			testClass.getAnnotatedMethods(BeforeClass.class);
			testClass.getAnnotatedMethods(AfterClass.class);
		}
	}
}
