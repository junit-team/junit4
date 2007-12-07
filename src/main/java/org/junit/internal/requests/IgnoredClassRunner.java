/**
 * 
 */
package org.junit.internal.requests;

import org.junit.Ignore;
import org.junit.Assume.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

public class IgnoredClassRunner extends Runner {
	private final Class<?> fTestClass;

	public IgnoredClassRunner(Class<?> testClass) {
		fTestClass= testClass;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestIgnored(getDescription());		
		notifier.fireTestIgnoredReason(getDescription(),
				new AssumptionViolatedException(fTestClass.getAnnotation(
						Ignore.class).value()));	
	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription(fTestClass);
	}
}