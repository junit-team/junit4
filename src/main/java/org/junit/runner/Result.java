package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assume.AssumptionViolatedException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.Ignorance;
import org.junit.runner.notification.InvalidAssumption;
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
	private List<InvalidAssumption> fInvalidAssumptions= new ArrayList<InvalidAssumption>();
	private long fRunTime= 0;
	private long fStartTime;

	private List<Ignorance> fIgnorances = new ArrayList<Ignorance>();

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
	 * @return the {@link Failure}s describing tests that failed and the problems they encountered
	 */
	public List<Failure> getFailures() {
		return fFailures;
	}

	public int getInvalidAssumptionCount() {
		return fInvalidAssumptions.size();
	}
	
	public List<InvalidAssumption> getInvalidAssumptions() {
		return fInvalidAssumptions;
	}

	/**
	 * @return the number of tests ignored during the run
	 */
	public int getIgnoreCount() {
		return fIgnoreCount;
	}

	public List<Ignorance> getIgnorances() {
		return fIgnorances;
	}

	/**
	 * @return <code>true</code> if all tests succeeded
	 */
	public boolean wasSuccessful() {
		return getFailureCount() == 0;
	}

	private class Listener extends RunListener {
		private boolean fAssumptionFailed = false;
		
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
		public void testFinished(Description description) throws Exception {
			if (!fAssumptionFailed)
				fCount++;
			fAssumptionFailed = false;
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			fFailures.add(failure);
		}

		@Override
		public void testIgnored(Description description, String reason) throws Exception {
			fIgnoreCount++;
			fIgnorances.add(new Ignorance(description, reason));
		}
		
		@Override
		public void testAssumptionInvalid(Description description,
				AssumptionViolatedException e) {
			fInvalidAssumptions.add(new InvalidAssumption(description, e));
			fAssumptionFailed = true;
		}
	}

	/**
	 * Internal use only.
	 */
	public RunListener createListener() {
		return new Listener();
	}
}
