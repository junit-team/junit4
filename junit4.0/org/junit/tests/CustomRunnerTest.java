package org.junit.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestClassRunner;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;

import static org.junit.Assert.*;

// TODO: better factoring here
public class CustomRunnerTest {
	public static class CustomRunner extends TestClassRunner {
		public CustomRunner(Class<?> klass) throws InitializationError {
			super(klass, new TestClassMethodsRunner(klass) {
				@Override
				protected TestMethodRunner createMethodRunner(Object test, Method method, RunNotifier notifier) {
					return new TestMethodRunner(test, method, notifier,
							methodDescription(method)) {
						@Override
						protected void executeMethodBody()
								throws IllegalAccessException,
								InvocationTargetException {
							super.executeMethodBody();
							assertGlobalStateIsValid();
						}
					};				
				}
			});
		}
	}

	private static void assertGlobalStateIsValid() {
		Assert.fail();
	}

	@RunWith(CustomRunner.class)
	public static class UsesGlobalState {
		@Test
		public void foo() {
		}
	}

	@Test
	public void failsWithGlobalState() {
		assertEquals(1, JUnitCore.runClasses(UsesGlobalState.class)
				.getFailureCount());
	}

}
