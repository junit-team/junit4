package org.junit.tests.experimental.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
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
	
//TODO extract commonality from ParallelSuite and ParallelRunner
	public static class ParallelSuite extends Suite {
		public ParallelSuite(RunnerBuilder builder, Class<?>[] classes) throws InitializationError {
			super(builder, classes);
		}
		private ExecutorService fService= Executors.newCachedThreadPool();
		private List<Future<Object>> fResults= new ArrayList<Future<Object>>();
	
		@Override
		protected void runChild(final Runner runner, final RunNotifier notifier) {
			Callable<Object> callable= new Callable<Object>() {
				public Object call() throws Exception {
					superRunChild(runner, notifier);
					return null;
				}
			};
			fResults.add(fService.submit(callable));
		}
		
		protected void superRunChild(Runner runner, RunNotifier notifier) {
			super.runChild(runner, notifier);
		}
		
		@Override
		public void run(RunNotifier notifier) {
			super.run(notifier);
			for (Future<Object> each : fResults)
				try {
					each.get(2000, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					e.printStackTrace();
				} 
		}
	}

	public static class ParallelRunner extends BlockJUnit4ClassRunner {
		public ParallelRunner(Class<?> klass) throws InitializationError {
			super(klass);
		}
		private ExecutorService fService= Executors.newCachedThreadPool();
		private List<Future<Object>> fResults= new ArrayList<Future<Object>>();
	
		@Override
		protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
			Callable<Object> callable= new Callable<Object>() {
				public Object call() throws Exception {
					superRunChild(method, notifier);
					return null;
				}
			};
			fResults.add(fService.submit(callable));
		}
		
		protected void superRunChild(FrameworkMethod method, RunNotifier notifier) {
			super.runChild(method, notifier);
		}
		
		@Override
		public void run(RunNotifier notifier) {
			super.run(notifier);
			for (Future<Object> each : fResults)
				try {
					each.get(2000, TimeUnit.MILLISECONDS); // TODO what should this really be?
				} catch (Exception e) {
					e.printStackTrace();
				} 
		}
	}

	@Override
	public Suite getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes) throws InitializationError {
		return fClasses
			? new ParallelSuite(builder, classes)
			: super.getSuite(builder, classes);
	}
	
	@Override
	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
			throws Throwable {
		return fMethods
			? new ParallelRunner(testClass)
			: super.getRunner(builder, testClass);
	}

	public static Computer methods() {
		return new ParallelComputer(false, true);
	}
}