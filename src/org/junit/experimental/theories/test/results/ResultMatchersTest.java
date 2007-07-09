package org.junit.experimental.theories.test.results;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.theories.matchers.api.StringContains.containsString;
import org.junit.Test;
import org.junit.experimental.results.ResultMatchers;
import org.junit.experimental.theories.methods.api.Theory;
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
}
