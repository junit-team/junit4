package junit.swingui;

import javax.swing.JTabbedPane;
import junit.framework.*;

/**
 * A TestRunView is shown as a page in a tabbed folder.
 * It contributes the page contents and can return
 * the currently selected tests. A TestRunView is 
 * notified about the start and finish of a run.
 */
interface TestRunView {
	/**
	 * Returns the currently selected Test in the View
	 */
	public Test getSelectedTest();
	/**
	 * Activates the TestRunView
	 */
	public void activate();
	/**
	 * Reveals the given failure
	 */
	public void revealFailure(Test failure);
	/**
	 * Adds the TestRunView to the test run views tab
	 */
	public void addTab(JTabbedPane pane);
	/**
	 * Informs that the suite is about to start 
	 */
	public void aboutToStart(Test suite, TestResult result);
	/**
	 * Informs that the run of the test suite has finished 
	 */
	public void runFinished(Test suite, TestResult result);
}