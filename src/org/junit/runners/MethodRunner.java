package org.junit.runners;

import org.junit.internal.runners.TestMethod;
import org.junit.runner.notification.RunNotifier;

public interface MethodRunner {
	public abstract void run(TestMethod method, RunNotifier notifier);
}
