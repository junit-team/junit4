package org.junit.runner.notification;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Result;

/**
 * If you write custom runners, you may need to notify JUnit of your progress running tests.
 * Do this by invoking the <code>RunNotifier</code> passed to your implementation of
 * {@link org.junit.runner.Runner#run(RunNotifier)}. Future evolution of this class is likely to 
 * move {@link #fireTestRunStarted(Description)} and {@link #fireTestRunFinished(Result)}
 * to a separate class since they should only be called once per run.
 */
public class RunNotifier {
	private final List<RunListener> fListeners= 
		Collections.synchronizedList(new ArrayList<RunListener>());
	private volatile boolean fPleaseStop= false;

	public RunNotifier() {
		Iterable<RunListener> externalListeners;
		try { externalListeners = ServiceLoader.load(RunListener.class); }
		catch (NoClassDefFoundError ignore) { return; }
		for (RunListener listener : externalListeners)
			fListeners.add(listener);
	}
	
	/** Internal use only
	 */
	public void addListener(RunListener listener) {
		fListeners.add(listener);
	}

	/** Internal use only
	 */
	public void removeListener(RunListener listener) {
		fListeners.remove(listener);
    }

	private abstract class SafeNotifier {
		private final List<RunListener> fCurrentListeners;

		SafeNotifier() {
			this(fListeners);
		}

		SafeNotifier(List<RunListener> currentListeners) {
			fCurrentListeners= currentListeners;
		}

		void run() {
			synchronized (fListeners) {
				List<RunListener> safeListeners= new ArrayList<RunListener>();
				List<Failure> failures= new ArrayList<Failure>();
				for (Iterator<RunListener> all= fCurrentListeners.iterator(); all
						.hasNext();)
					try {
						RunListener listener= all.next();
						notifyListener(listener);
						safeListeners.add(listener);
					} catch (Exception e) {
						failures.add(new Failure(Description.TEST_MECHANISM, e));
					}
				fireTestFailures(safeListeners, failures);
			}
		}
		
		abstract protected void notifyListener(RunListener each) throws Exception;
	}
	
	/**
	 * Do not invoke. 
	 */
	public void fireTestRunStarted(final Description description) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunStarted(description);
			};
		}.run();
	}
	
	/**
	 * Do not invoke.
	 */
	public void fireTestRunFinished(final Result result) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testRunFinished(result);
			};
		}.run();
	}
	
	/**
	 * Invoke to tell listeners that an atomic test is about to start.
	 * @param description the description of the atomic test (generally a class and method name)
	 * @throws StoppedByUserException thrown if a user has requested that the test run stop
	 */
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

	/**
	 * Invoke to tell listeners that an atomic test failed.
	 * @param failure the description of the test that failed and the exception thrown
	 */
	public void fireTestFailure(Failure failure) {
		fireTestFailures(fListeners, asList(failure));
	}

	private void fireTestFailures(List<RunListener> listeners,
			final List<Failure> failures) {
		if (!failures.isEmpty())
			new SafeNotifier(listeners) {
				@Override
				protected void notifyListener(RunListener listener)
						throws Exception {
					for (Failure each : failures)
						listener.testFailure(each);
				};
			}.run();
	}

	/**
	 * Invoke to tell listeners that an atomic test flagged that it assumed
	 * something false.
	 * 
	 * @param failure
	 *            the description of the test that failed and the
	 *            {@link AssumptionViolatedException} thrown
	 */
	public void fireTestAssumptionFailed(final Failure failure) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testAssumptionFailure(failure);
			};
		}.run();
	}

	/**
	 * Invoke to tell listeners that an atomic test was ignored.
	 * @param description the description of the ignored test
	 */
	public void fireTestIgnored(final Description description) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testIgnored(description);
			}
		}.run();
	}

	/**
	 * Invoke to tell listeners that an atomic test finished. Always invoke 
	 * {@link #fireTestFinished(Description)} if you invoke {@link #fireTestStarted(Description)} 
	 * as listeners are likely to expect them to come in pairs.
	 * @param description the description of the test that finished
	 */
	public void fireTestFinished(final Description description) {
		new SafeNotifier() {
			@Override
			protected void notifyListener(RunListener each) throws Exception {
				each.testFinished(description);
			};
		}.run();
	}
	
	/**
	 * Ask that the tests run stop before starting the next test. Phrased politely because
	 * the test currently running will not be interrupted. It seems a little odd to put this
	 * functionality here, but the <code>RunNotifier</code> is the only object guaranteed 
	 * to be shared amongst the many runners involved.
	 */
	public void pleaseStop() {
		fPleaseStop= true;
	}

	/**
	 * Internal use only. The Result's listener must be first.
	 */
	public void addFirstListener(RunListener listener) {
		fListeners.add(0, listener);
	}
}