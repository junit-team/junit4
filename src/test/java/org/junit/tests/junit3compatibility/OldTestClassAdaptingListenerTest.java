package org.junit.tests.junit3compatibility;

import static org.junit.Assert.assertEquals;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestListener;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class OldTestClassAdaptingListenerTest {
	@Test
	public void addFailureDelegatesToNotifier() {
		Result result= new Result();
		RunListener listener= result.createListener();
		RunNotifier notifier= new RunNotifier();
		notifier.addFirstListener(listener);
		TestCase testCase= new TestCase() {
		};
		TestListener adaptingListener= new JUnit38ClassRunner(testCase)
				.createAdaptingListener(notifier);
		adaptingListener.addFailure(testCase, new AssertionFailedError());
		assertEquals(1, result.getFailureCount());
	}
}
