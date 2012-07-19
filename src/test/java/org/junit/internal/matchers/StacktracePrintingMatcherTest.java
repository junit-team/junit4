package org.junit.internal.matchers;

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.internal.matchers.StacktracePrintingMatcher.withStacktrace;
import org.junit.Test;

public class StacktracePrintingMatcherTest {
	
	@Test
	public void succeedsWhenInnerMatcherSuceeds() throws Exception {
		assertTrue(withStacktrace(is(any(Throwable.class))).matches(new Exception()));
	}
	
	@Test
	public void failsWhenInnerMatcherFails() throws Exception {
		assertFalse(withStacktrace(is(notNullValue(Exception.class))).matches(null));
	}

	@Test
	public void assertThatIncludesStacktrace() {
		Exception actual= new IllegalArgumentException("my message");
		Exception expected= new NullPointerException();

		try {
			assertThat(actual, withStacktrace(is(expected)));
		} catch (AssertionError e) {
			assertThat(e.getMessage(), containsString("Stacktrace was: java.lang.IllegalArgumentException: my message"));
		}
	}
}
