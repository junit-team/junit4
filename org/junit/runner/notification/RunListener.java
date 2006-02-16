package org.junit.runner.notification;

import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * If you need to respond to the events during a test run, extend <code>RunListener</code>
 * and override the appropriate methods. If a listener throws an exception while processing a 
 * test event, it will be removed for the remainder of the test run.
 * <p>
 * For example, suppose you have a <code>Cowbell</code>
 * class that you want to make a noise whenever a test fails. You could write:<br>
 * <code>
 * public class RingingListener extends RunListener {
 * %nbsp;%nbsp;public void testFailure(Failure failure) {
 * %nbsp;%nbsp;%nbsp;%nbsp;Cowbell.ring();
 * %nbsp;%nbsp;}
 * }
 * </code>
 * <p>
 * To invoke your listener, you need to run your tests through <code>JUnitCore</code>. <br>
 * <code>
 * public void main(String... args) {
 * %nbsp;%nbsp;JUnitCore core= new JUnitCore();
 * %nbsp;%nbsp;core.addListener(new RingingListener());
 * %nbsp;%nbsp;
 * core.run(MyTestClass.class);
 * }
 * </code>
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
	 * @param description the description of the test that is about to be run (generally a class and method name)
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
	 * Called when a test will not be run, generally because a test method is annotated with <code>@Ignored</code>.
	 * @param description describes the test that will not be run
	 */
	public void testIgnored(Description description) throws Exception {
	}

}


