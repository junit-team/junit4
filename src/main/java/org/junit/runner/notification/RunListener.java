package org.junit.runner.notification;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * <p>If you need to respond to the events during a test run, extend <code>RunListener</code>
 * and override the appropriate methods. If a listener throws an exception while processing a 
 * test event, it will be removed for the remainder of the test run.</p>
 * 
 * <p>For example, suppose you have a <code>Cowbell</code>
 * class that you want to make a noise whenever a test fails. You could write:
 * <pre>
 * public class RingingListener extends RunListener {
 *    public void testFailure(Failure failure) {
 *       Cowbell.ring();
 *    }
 * }
 * </pre>
 * </p>
 * 
 * <p>To invoke your listener, you need to run your tests through <code>JUnitCore</code>.
 * <pre>
 * public void main(String... args) {
 *    JUnitCore core= new JUnitCore();
 *    core.addListener(new RingingListener());
 *    core.run(MyTestClass.class);
 * }
 * </pre>
 * </p>
 * @see org.junit.runner.JUnitCore
 */
public class RunListener {

	/**
	 * Called before any tests have been run.
	 * @param description describes the tests to be run
	 */
	public void testRunStarted(Description description) throws Exception {
	}
	
	/**
	 * Called when all tests have finished
	 * @param result the summary of the test run, including all the tests that failed
	 */
	public void testRunFinished(Result result) throws Exception {
	}
	
	/**
	 * Called when an atomic test is about to be started.
	 * @param description the description of the test that is about to be run 
	 * (generally a class and method name)
	 */
	public void testStarted(Description description) throws Exception {
	}

	/**
	 * Called when an atomic test has finished, whether the test succeeds or fails.
	 * @param description the description of the test that just ran
	 */
	public void testFinished(Description description) throws Exception {
	}

	/** 
	 * Called when an atomic test fails.
	 * @param failure describes the test that failed and the exception that was thrown
	 */
	public void testFailure(Failure failure) throws Exception {
	}

	/**
	 * Called when an atomic test flags that it assumes a condition that is
	 * false
	 * 
	 * @param failure
	 *            describes the test that failed and the
	 *            {@link AssumptionViolatedException} that was thrown
	 */
	public void testAssumptionFailure(Failure failure) {
	}

	/**
	 * Called when a test will not be run, generally because a test method is annotated 
	 * with {@link org.junit.Ignore}.
	 * 
	 * @param description describes the test that will not be run
	 */
	public void testIgnored(Description description) throws Exception {
	}
}


