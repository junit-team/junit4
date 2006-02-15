package org.junit.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import static org.junit.Assert.assertEquals;

public class ValidationTest {
	public static class WrongBeforeClass {
		@BeforeClass
		protected int a() {
			return 0;
		}
	}

	@Test(expected=InitializationError.class)
	public void testClassRunnerHandlesBeforeClassAndAfterClassValidation() throws InitializationError {
		new TestClassRunner(WrongBeforeClass.class, new Runner() {
			@Override
			public Description getDescription() {
				return null;
			}

			@Override
			public void run(RunNotifier notifier) {
				// do nothing
			}
		});
	}
	
	@Test
	public void initializationErrorIsOnCorrectClass() {
		assertEquals(WrongBeforeClass.class.getName(), 
				Request.aClass(WrongBeforeClass.class).getRunner().getDescription().getDisplayName());
	}
}
