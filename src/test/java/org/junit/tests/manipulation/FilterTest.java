package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class FilterTest {
	public static class NamedFilter extends Filter {
		private final String fName;

		public NamedFilter(String name) {
			fName= name;
		}

		@Override
		public boolean shouldRun(Description description) {
			return false;
		}

		@Override
		public String describe() {
			return fName;
		}
	}

	@Test
	public void intersectionText() {
		NamedFilter a= new NamedFilter("a");
		NamedFilter b= new NamedFilter("b");
		assertEquals("a and b", a.intersect(b).describe());
		assertEquals("b and a", b.intersect(a).describe());
	}
}
