package org.junit.experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

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

	private static <T> Runner parallelize(Runner runner) {
		if (runner instanceof ParentRunner<?>) {
			((ParentRunner<?>) runner).setScheduler(new RunnerScheduler() {
				private final List<Future<Object>> fResults= new ArrayList<Future<Object>>();

				private final ExecutorService fService= Executors
						.newCachedThreadPool();

				public void schedule(final Runnable childStatement) {
					fResults.add(fService.submit(new Callable<Object>() {
						public Object call() throws Exception {
							childStatement.run();
							return null;
						}
					}));
				}

				public void finished() {
					for (Future<Object> each : fResults)
						try {
							each.get();
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			});
		}
		return runner;
	}

	@Override
	public Runner getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes)
			throws InitializationError {
		Runner suite= super.getSuite(builder, classes);
		return fClasses ? parallelize(suite) : suite;
	}

	@Override
	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
			throws Throwable {
		Runner runner= super.getRunner(builder, testClass);
		return fMethods ? parallelize(runner) : runner;
	}
}
