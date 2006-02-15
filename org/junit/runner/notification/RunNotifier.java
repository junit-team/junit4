package org.junit.runner.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;

public class RunNotifier {
	private List<RunListener> fListeners= new ArrayList<RunListener>();
	private boolean fPleaseStop= false;
	
	public void addListener(RunListener listener) {
		fListeners.add(listener);
	}

	public void removeListener(RunListener listener) {
		fListeners.remove(listener);
	}

	private abstract class SafeNotifier {
		void run() {
			for (Iterator<RunListener> all= fListeners.iterator(); all.hasNext();) {
				try {
					notifyListener(all.next());
				} catch (Exception e) {
					all.remove();
					fireTestFailure(new Failure(Description.TEST_MECHANISM, e)); // Remove the offending listener first to avoid an infinite loop
				}
			}
		}
		
		abstract protected void notifyListener(RunListener each) throws Exception;
	}
	
	public void fireTestRunStarted(final Description description) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunStarted(description);
			};
		}.run();
	}
	
	public void fireTestRunFinished(final Result result) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunFinished(result);
			};
		}.run();
	}
	
	public void fireTestStarted(final Description description) throws StoppedByUserException {
		if (fPleaseStop)
			throw new StoppedByUserException();
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testStarted(description);
			};
		}.run();
	}

	public void fireTestFailure(final Failure failure) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testFailure(failure);
			};
		}.run();
	}

	public void fireTestIgnored(final Description description) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testIgnored(description);
			};
		}.run();
	}

	public void fireTestFinished(final Description description) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testFinished(description);
			};
		}.run();
	}
	
	public void pleaseStop() {
		fPleaseStop= true;
	}
}