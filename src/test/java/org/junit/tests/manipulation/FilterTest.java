package org.junit.tests.manipulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
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
	
	@Test
	public void intersectSelf() {
		NamedFilter a= new NamedFilter("a");
		assertSame(a, a.intersect(a));
	}
	
	@Test
	public void intersectAll() {
		NamedFilter a= new NamedFilter("a");
		assertSame(a, a.intersect(Filter.ALL));
		assertSame(a, Filter.ALL.intersect(a));
		assertSame(Filter.ALL, Filter.ALL.intersect(Filter.ALL));
	}
}
