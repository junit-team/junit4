package org.junit.runner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * A <code>Result</code> collects and summarizes information from running multiple
 * tests. Since tests are expected to run correctly, successful tests are only noted in
 * the count of tests that ran.
 */
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;
	private AtomicInteger fCount = new AtomicInteger();
	private AtomicInteger fIgnoreCount= new AtomicInteger();
	private final List<Failure> fFailures= Collections.synchronizedList(new ArrayList<Failure>());
	private long fRunTime= 0;
	private long fStartTime;

	/**
	 * @return the number of tests run
	 */
	public int getRunCount() {
		return fCount.get();
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

	/**
	 * @return the number of tests ignored during the run
	 */
	public int getIgnoreCount() {
		return fIgnoreCount.get();
	}

	/**
	 * @return <code>true</code> if all tests succeeded
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
		public void testFinished(Description description) throws Exception {
			fCount.getAndIncrement();
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			fFailures.add(failure);
		}

		@Override
		public void testIgnored(Description description) throws Exception {
			fIgnoreCount.getAndIncrement();
		}

		@Override
		public void testAssumptionFailure(Failure failure) {
			// do nothing: same as passing (for 4.5; may change in 4.6)
		}
	}

	/**
	 * Internal use only.
	 */
	public RunListener createListener() {
		return new Listener();
	}
}
