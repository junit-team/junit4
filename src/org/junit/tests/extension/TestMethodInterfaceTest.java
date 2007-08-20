package org.junit.tests.extension;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.TestClass;
import org.junit.internal.runners.TestMethod;

public class TestMethodInterfaceTest {
	public static class BeforesAndAfters {
		@Before
		public void before() {
		}

		@After
		public void after() {
		}

		@Test
		public void test() {
		}
	}

	@Test
	public void getBeforesIsPublic() throws SecurityException,
			NoSuchMethodException {
		TestMethod testMethod= new TestMethod(BeforesAndAfters.class
				.getMethod("test"), new TestClass(BeforesAndAfters.class));
		assertThat(testMethod.getBefores().size(), is(1));
		assertThat(testMethod.getAfters().size(), is(1));
	}
}
