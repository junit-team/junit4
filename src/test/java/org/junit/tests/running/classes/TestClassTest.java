package org.junit.tests.running.classes;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

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
	
	private static int fComputations;
	// Profiling a JUnit 4.4 suite shows that getAnnotatedMethods accounts for at least 13% of running time
	// (all running time, including user test code!)
	@Test
	public void annotationsAreCached() {
		TestClass testClass= new TestClass(ManyMethods.class) {			
			@Override
			protected Annotation[] computeAnnotations(FrameworkMethod testMethod) {
				fComputations++;
				return super.computeAnnotations(testMethod);
			}
		};
		testClass.getAnnotatedMethods(Test.class);
		fComputations= 0;
		testClass.getAnnotatedMethods(Test.class);
		assertEquals(0, fComputations);
	}
}
