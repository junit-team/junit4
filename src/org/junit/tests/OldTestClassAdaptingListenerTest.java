package org.junit.tests;

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
		TestListener adaptingListener= JUnit38ClassRunner
				.createAdaptingListener(notifier);
		TestCase testCase= new TestCase() {
		};
		adaptingListener.addFailure(testCase, new AssertionFailedError());
		assertEquals(1, result.getFailureCount());
	}
}
