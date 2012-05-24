package org.junit.tests.assertion;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.causedBy;
import org.junit.Test;

public class CauseMatcherTest {
	@Test
	public void causedByCorrect() {
		assertThat(new IllegalStateException(new IllegalArgumentException()), causedBy(instanceOf(IllegalArgumentException.class)));
	}	
	
	@Test(expected=AssertionError.class)
	public void causedByFailing() {
		assertThat(new IllegalStateException(new IllegalArgumentException()), causedBy(instanceOf(IllegalStateException.class)));
	}	
}
