package org.junit.runner;

import org.junit.runner.notification.RunNotifier;

/**
 * A <code>Runner</code> runs tests and notifies a <code>RunNotifier</code>
 * of significant events as it does so. You will need to subclass <code>Runner</code>
 * when using <code>@RunWith</code> to invoke a custom runner. When creating
 * a custom runner, in addition to implementing the abstract methods here you must
 * also provide a constructor that takes as an argument the <code>Class</code> containing
 * the tests.
 * 
 * @see org.junit.runner.Description
 * @see org.junit.runner.RunWith
 */
public abstract class Runner {
	/**
	 * @return a <code>Description</code> showing the tests to be run by the receiver
	 */
	public abstract Description getDescription();

	/**
	 * Run the tests for this runner.
	 * @param notifier will be notified of events while tests are being run--tests being started, finishing, and failing
	 */
	public abstract void run(RunNotifier notifier);
	
	/**
	 * @return the number of tests to be run by the receiver
	 */
	public int testCount() {
		return getDescription().testCount();
	}
}