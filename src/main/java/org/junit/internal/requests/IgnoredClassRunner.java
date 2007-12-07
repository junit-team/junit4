/**
 * 
 */
package org.junit.internal.requests;

import static org.hamcrest.CoreMatchers.nullValue;
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
		
		// TODO: (Dec 7, 2007 11:11:13 AM) DUP of an ugly idiom
		notifier.fireTestIgnoredReason(getDescription(),
				new AssumptionViolatedException(fTestClass.getAnnotation(
						Ignore.class).value(), nullValue()));	
	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription(fTestClass);
	}
}