// Copyright 2010 Google Inc. All Rights Reserved.

package org.junit.tests.assertion;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runners.model.MultipleFailureException;

/**
 * Tests for {@link org.junit.runners.model.MultipleFailureException}
 *
 * @author kcooney@google.com (Kevin Cooney)
 */
public class MultipleFailureExceptionTest {

	@Test
	public void assertEmptyDoesNotThrowForEmptyList() throws Throwable {
		MultipleFailureException.assertEmpty(Collections.<Throwable>emptyList());
	}
	
	@Test(expected=ExpectedException.class)
	public void assertEmptyRethrowsSingleThrowable() throws Throwable {
		MultipleFailureException.assertEmpty(
				Collections.<Throwable>singletonList(new ExpectedException("pesto")));
	}
	
	@Test
	public void assertEmptyThrowsMutipleFailureExceptionForManyThrowables() throws Throwable {
		List<Throwable> errors = new ArrayList<Throwable>();
		errors.add(new ExpectedException("basil"));
		errors.add(new RuntimeException("garlic"));
		
		try {
			MultipleFailureException.assertEmpty(errors);
			fail();
		} catch (MultipleFailureException expected) {
			assertThat(expected.getFailures(), equalTo(errors));
			assertTrue(expected.getMessage().startsWith("There were 2 errors:\n"));
			assertTrue(expected.getMessage().contains("ExpectedException(basil)\n"));
			assertTrue(expected.getMessage().contains("RuntimeException(garlic)"));
		}
	}


	private static class ExpectedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ExpectedException(String message) {
			super(message);
		}
	}
}
