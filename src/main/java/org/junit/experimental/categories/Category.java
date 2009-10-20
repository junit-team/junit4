package org.junit.experimental.categories;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a test class or test method as belonging to one or more categories of tests.
 * The value is an array of interface classes that extend the {@link CategoryType}
 * superinterface.
 * 
 * This annotation is only interpreted by the Categories runner (at present).
 * 
 * For example:
<pre>
	public interface FastTests extends CategoryType {}
	public interface SlowTests extends CategoryType {}

	public static class A {
		@Test
		public void a() {
			fail();
		}

		@Category(SlowTests.class)
		@Test
		public void b() {
		}
	}

	@Category({SlowTests.class, FastTests.class})
	public static class B {
		@Test
		public void c() {

		}
	}
</pre>
 * 
 * For more usage, see code example on {@link Categories}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Category {
	Class<? extends CategoryType>[] value();
}