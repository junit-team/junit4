package org.junit.tests.running.classes;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.junit.Test;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class BlockJUnit4ClassRunnerTest {

	public static class FailForAssertion {
		@Test(expected= NullPointerException.class)
		public void failingTest() {
			fail("Failed because of assertion.");
			throw new NullPointerException();
		}
	}

	@Test
	public void handOverAssertionError() throws Exception {
		Result result= testResult(FailForAssertion.class);
		assertEquals("Failed because of assertion.", result.getFailures()
				.get(0).getMessage());
	}

	private Result testResult(Class<?> test) throws InitializationError {
		Result result= new Result();
		RunNotifier notifier= new RunNotifier();
		notifier.addListener(result.createListener());
		BlockJUnit4ClassRunner runner= new BlockJUnit4ClassRunner(test);
		runner.run(notifier);
		return result;
	}
}
