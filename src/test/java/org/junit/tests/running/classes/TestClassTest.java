package org.junit.tests.running.classes;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
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
}
