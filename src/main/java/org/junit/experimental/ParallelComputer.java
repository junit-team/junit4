package org.junit.experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class ParallelComputer extends Computer {
	private final boolean fClasses;

	private final boolean fMethods;

	public ParallelComputer(boolean classes, boolean methods) {
		fClasses= classes;
		fMethods= methods;
	}

	public static Computer classes() {
		return new ParallelComputer(true, false);
	}

	public static Computer methods() {
		return new ParallelComputer(false, true);
	}

	private static <T> Runner parallelize(ParentRunner<T> runner)
			throws InitializationError {
		return new ParallelParentRunner<T>(runner);
	}

	private static class ParallelParentRunner<T> extends ParentRunner<T> {
		private final ParentRunner<T> fDelegate;

		private final List<Future<Object>> fResults= new ArrayList<Future<Object>>();

		private final ExecutorService fService= Executors.newCachedThreadPool();

		public ParallelParentRunner(ParentRunner<T> delegate)
				throws InitializationError {
			super(delegate.getTestClass().getJavaClass());
			fDelegate= delegate;
		}

		@Override
		public Description describeChild(T child) {
			// TODO (May 4, 2009 4:34:05 PM): How to do this for real?
			return fDelegate.internalDescribeChild(child);
		}

		@Override
		public List<T> getChildren() {
			return fDelegate.internalGetChildren();
		}

		@Override
		public void runChild(final T child, final RunNotifier notifier) {
			fResults.add(fService.submit(new Callable<Object>() {
				public Object call() throws Exception {
					fDelegate.internalRunChild(child, notifier);
					return null;
				}
			}));
		}

		@Override
		public void run(RunNotifier notifier) {
			super.run(notifier);
			for (Future<Object> each : fResults)
				try {
					each.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public Runner getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes)
			throws InitializationError {
		Suite suite= (Suite) super.getSuite(builder, classes);
		return fClasses ? parallelize(suite) : suite;
	}

	@Override
	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
			throws Throwable {
		// TODO (May 4, 2009 4:09:16 PM): no guarantees here
		BlockJUnit4ClassRunner runner= (BlockJUnit4ClassRunner) super.getRunner(builder, testClass);
		return fMethods ? parallelize(runner) : runner;
	}
}
