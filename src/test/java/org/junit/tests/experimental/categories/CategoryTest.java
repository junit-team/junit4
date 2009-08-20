package org.junit.tests.experimental.categories;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

public class CategoryTest {
	// TODO: move
	public class FilterRule {

	}

	public static class CategoryFilter {
		public static CategoryFilter include(
				Class<? extends CategoryClass> class1) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public interface CategoryClass {

	}

	public static class SlowTests implements CategoryClass {

	}

	public @interface Category {
		Class<? extends CategoryClass> value();
	}

	public static class A {
		@Test public void a() {
			fail();
		}

		@Category(SlowTests.class)
		@Test public void b() {
		}
	}

	@Category(SlowTests.class)
	public static class B {
		@Test public void c() {

		}
	}

	public static class C {
		@Test public void d() {
			fail();
		}
	}

	@RunWith(Suite.class)
	@SuiteClasses( { A.class, B.class, C.class })
	public static class SlowTestSuite {
		@Rule public CategoryFilter filter = CategoryFilter.include(SlowTests.class);
	}
	
	@Test public void testCount() {
		assertThat(testResult(SlowTestSuite.class), isSuccessful());
	}
}
