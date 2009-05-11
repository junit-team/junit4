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
import org.junit.tests.SafeStatement;

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

	private static void parallelize(Runner runner) {
		((ParentRunner<?>) runner).installDecorator(new ParentRunner.Decorator() {
			private final List<Future<Object>> fResults= new ArrayList<Future<Object>>();

			private final ExecutorService fService= Executors
					.newCachedThreadPool();

			public void runAll(SafeStatement statement) {
				statement.execute();
				for (Future<Object> each : fResults)
					try {
						each.get();
					} catch (Exception e) {
						e.printStackTrace();
					}

			}

			public void runChild(final SafeStatement statement) {
				fResults.add(fService.submit(new Callable<Object>() {
					public Object call() throws Exception {
						statement.execute();
						return null;
					}
				}));
			}
		});
	}
	
	@Override
	protected Runner modify(Runner runner) {
		if (shouldParallelize(runner))
			parallelize(runner);
		return runner;
	}

	private boolean shouldParallelize(Runner runner) {
		if (runner instanceof ParentRunner) {
			ParentRunner<?> parentRunner= (ParentRunner<?>) runner;
			if (fClasses && parentRunner.isSuite())
				return true;
			if (fMethods && !parentRunner.isSuite())
				return true;
		}
		return false;
	}
}
