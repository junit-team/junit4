package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * A <code>Result</code> collects and summarizes information from running multiple
 * tests. Since tests are expected to run correctly, successful tests are only noted in
 * the count of tests that ran.
 */
public class Result {
	private int fCount= 0;
	private int fIgnoreCount= 0;
	private List<Failure> fFailures= new ArrayList<Failure>();
	private long fRunTime= 0;
	private long fStartTime;

	/**
	 * @return the number of tests run
	 */
	public int getRunCount() {
		return fCount;
	}

	/**
	 * @return the number of tests that failed during the run
	 */
	public int getFailureCount() {
		return fFailures.size();
	}

	/**
	 * @return the number of milliseconds it took to run the entire suite to run
	 */
	public long getRunTime() {
		return fRunTime;
	}

	/**
	 * @return the <code>Failures</code> describing tests that failed and the problems they encountered
	 */
	public List<Failure> getFailures() {
		return fFailures;
	}

	/**
	 * @return the number of tests ignored during the run
	 */
	public int getIgnoreCount() {
		return fIgnoreCount;
	}

	/**
	 * @return true if all tests succeeded
	 */
	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

	private class Listener extends RunListener {
		@Override
		public void testRunStarted(Description description) throws Exception {
			fStartTime= System.currentTimeMillis();
		}

		@Override
		public void testRunFinished(Result result) throws Exception {
			long endTime= System.currentTimeMillis();
			fRunTime+= endTime - fStartTime;
		}

		@Override
		public void testStarted(Description description) throws Exception {
			fCount++;
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			fFailures.add(failure);
		}

		@Override
		public void testIgnored(Description description) throws Exception {
			fIgnoreCount++;
		}
	}

	/**
	 * Internal use only.
	 */
	public RunListener createListener() {
		return new Listener();
	}
}
