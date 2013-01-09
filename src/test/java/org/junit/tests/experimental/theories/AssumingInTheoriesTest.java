package org.junit.tests.experimental.theories;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;

@RunWith(Theories.class)
public class AssumingInTheoriesTest {

	@Test
	public void noTheoryAnnotationMeansAssumeShouldIgnore() {
		Assume.assumeTrue(false);
	}

	@Test
	public void theoryMeansOnlyAssumeShouldFail() throws InitializationError {
		JUnitCore junitRunner = new JUnitCore();
		Runner theoryRunner = new Theories(TheoryWithNoUnassumedParameters.class);
		Request request = Request.runner(theoryRunner);
		Result result = junitRunner.run(request);
		Assert.assertEquals(1, result.getFailureCount());
	}

	/**
	 * Simple class that SHOULD fail because no parameters are met.
	 */
	public static class TheoryWithNoUnassumedParameters {
		
		@DataPoint 
		public final static boolean FALSE = false;
		
		@Theory
		public void theoryWithNoUnassumedParameters(boolean value) {
			Assume.assumeTrue(value);
		}
	}

}
