package org.junit.tests.experimental.results;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.ResultMatchers.causedBy;
import static org.junit.experimental.results.ResultMatchers.failureIs;
import static org.junit.internal.matchers.StringContains.containsString;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.Theory;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
public class ResultMatchersTest {
	@Test
	public void hasFailuresHasGoodDescription() {
		assertThat(ResultMatchers.failureCountIs(3).toString(),
				is("has 3 failures"));
	}

	@Theory
	public void hasFailuresDescriptionReflectsInput(int i) {
		assertThat(ResultMatchers.failureCountIs(i).toString(),
				containsString("" + i));
	}
	
	private Result createResult() throws Exception {
		Result result= new Result();
		result.createListener().testFailure(new Failure(null, new IllegalArgumentException()));
		return result;
	}

	@Test
	public void failureIsCorrect() throws Exception {
		assertThat(createResult(), failureIs(instanceOf(IllegalArgumentException.class)));
	}
	
	@Test(expected=AssertionError.class)
	public void failureIsFailing() throws Exception {
		assertThat(createResult(), failureIs(instanceOf(IllegalStateException.class)));
	}
	
	@Test
	public void causedByCorrect() {
		assertThat(new IllegalStateException(new IllegalArgumentException()), causedBy(instanceOf(IllegalArgumentException.class)));
	}	
	
	@Test(expected=AssertionError.class)
	public void causedByFailing() {
		assertThat(new IllegalStateException(new IllegalArgumentException()), causedBy(CoreMatchers.instanceOf(IllegalStateException.class)));
	}	
}
