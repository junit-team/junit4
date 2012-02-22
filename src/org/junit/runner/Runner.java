package org.junit.runner;

import org.junit.runner.notification.RunNotifier;

/**
 * A <code>Runner</code> runs tests and notifies a {@link org.junit.runner.notification.RunNotifier}
 * of significant events as it does so. You will need to subclass <code>Runner</code>
 * when using {@link org.junit.runner.RunWith} to invoke a custom runner. When creating
 * a custom runner, in addition to implementing the abstract methods here you must
 * also provide a constructor that takes as an argument the {@link Class} containing
 * the tests.
 * <p/>
 * The default runner implementation guarantees that the instances of the test case
 * class will be constructed immediately before running the test and that the runner
 * will retain no reference to the test case instances, generally making them 
 * available for garbage collection.
 * 
 * @see org.junit.runner.Description
 * @see org.junit.runner.RunWith
 */
public abstract class Runner implements Describable {
	/* (non-Javadoc)
	 * @see org.junit.runner.Describable#getDescription()
	 */
	public abstract Description getDescription();

	/**
	 * Run the tests for this runner.
	 * @param notifier will be notified of events while tests are being run--tests being 
	 * started, finishing, and failing
	 */
	public abstract void run(RunNotifier notifier);
	
	/**
	 * @return the number of tests to be run by the receiver
	 */
	public int testCount() {
		return getDescription().testCount();
	}
}