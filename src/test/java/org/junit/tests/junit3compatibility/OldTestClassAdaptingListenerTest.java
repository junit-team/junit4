package org.junit.tests.junit3compatibility;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestListener;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

import static org.junit.Assert.assertEquals;

public class OldTestClassAdaptingListenerTest {
    @Test
    public void addFailureDelegatesToNotifier() {
        Result result = new Result();
        RunNotifier notifier = new RunNotifier(result.createListener());
        TestCase testCase = new TestCase() {
        };
        TestListener adaptingListener = new JUnit38ClassRunner(testCase)
                .createAdaptingListener(notifier);
        adaptingListener.addFailure(testCase, new AssertionFailedError());
        assertEquals(1, result.getFailureCount());
    }
}
