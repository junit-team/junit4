package org.junit.tests.running.classes;

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
}
