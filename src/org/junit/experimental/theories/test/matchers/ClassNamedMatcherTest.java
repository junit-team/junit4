/**
 * 
 */
package org.junit.experimental.theories.test.matchers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.experimental.theories.matchers.api.ClassNamedMatcher;

public class ClassNamedMatcherTest {
	public static class NothingMuch extends ClassNamedMatcher<Object> {
		public boolean matches(Object item) {
			return false;
		}
	}

	@Test public void classNamedMatcherWorks() {
		assertThat(new NothingMuch().toString(), is("nothing much"));
	}

	public static class NothingMuchMore extends ClassNamedMatcher<Object> {
		public boolean matches(Object item) {
			return false;
		}
	}

	@Test public void classNamedMatcherWorksTriangulation() {
		assertThat(new NothingMuchMore().toString(),
				is("nothing much more"));
	}
}