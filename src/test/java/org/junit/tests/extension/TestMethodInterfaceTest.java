package org.junit.tests.extension;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.model.TestClass;
import org.junit.internal.runners.model.TestMethod;

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
	public void getBeforesIsPublic() throws SecurityException {
		TestMethod testMethod= new TestMethod(new TestClass(
				BeforesAndAfters.class));
		assertThat(testMethod.getBefores().size(), is(1));
		assertThat(testMethod.getAfters().size(), is(1));
	}
}
