package org.junit.tests.experimental.results;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.ResultMatchers.failureIs;
import static org.junit.internal.matchers.StringContains.containsString;

import java.util.Arrays;

import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.Theory;
import org.junit.runner.Description;
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
	
	private PrintableResult createPrintableResult() throws Exception {
		return new PrintableResult(Arrays.asList(new Failure(Description.TEST_MECHANISM, new IllegalArgumentException())));
	}

	@Test
	public void failureIsCorrect() throws Exception {
		assertThat(createPrintableResult(), failureIs(instanceOf(IllegalArgumentException.class)));
	}
	
	@Test(expected=AssertionError.class)
	public void failureIsFailing() throws Exception {
		assertThat(createPrintableResult(), failureIs(instanceOf(IllegalStateException.class)));
	}
}
