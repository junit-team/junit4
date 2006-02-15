package org.junit.runner;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class Result {
	private int fCount= 0;
	private int fIgnoreCount= 0;
	private List<Failure> fFailures= new ArrayList<Failure>();
	private long fRunTime= 0;
	private long fStartTime;

	public int getRunCount() {
		return fCount;
	}

	public int getFailureCount() {
		return fFailures.size();
	}

	public long getRunTime() {
		return fRunTime;
	}

	public List<Failure> getFailures() {
		return fFailures;
	}

	public int getIgnoreCount() {
		return fIgnoreCount;
	}

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

	public RunListener createListener() {
		return new Listener();
	}
}
