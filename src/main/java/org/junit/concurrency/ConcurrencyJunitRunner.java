package org.junit.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Ignore;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * <p>Set this class as your junit (4) runner for each class you want to test
 * in parallel environment with this class annotation:</p>
 * 
 * <p><code>@RunWith(ConcurrencyJunitRunner.class</code></p>
 * 
 * <p>Notice: Junit 4 will load (or check) this annotation also automatically
 * from all super classes!</p>
 * 
 * <p>The number of execution times and used threads are configurable
 * with the annotation {@link Concurrency @Concurrency}. This annotation will
 * be also automatically loaded from all super classes and could be overridden
 * for each unit test method. The default values for
 * {@link Concurrency#times()} and {@link Concurrency#parallelThreads()} are
 * defined as constants in the class {@link ConcurrencyConfiguration}:</p>
 * 
 * <ul>
 * <li>{@link ConcurrencyConfiguration#TIMES_DEFAULT TIMES_DEFAULT} = 10</li>
 * <li>{@link ConcurrencyConfiguration#THREADS_DEFAULT THREADS_DEFAULT} = 16</li>
 * </ul>
 * 
 * <p>It is also possible to expect different behaviors, for example that
 * {@link Concurrency#expectAtLeast() at least one thread throw an exception}.
 * </p>
 * 
 * @author Christoph Jerolimov
 */
public final class ConcurrencyJunitRunner extends BlockJUnit4ClassRunner {

	/**
	 * Notifier for the junit test framework.
	 * <small>Notifier works like an listener without an interface, yet.</small>
	 */
	private RunnerNotifyHandler runnerNotifyHandler;

	/**
	 * Handler for the parameter {@link Concurrency#expectAtLeast()).
	 * <small>Notifier works like an listener without an interface, yet.</small>
	 */
	private ExpectAtLeastHandler expectAtLeastHandler;
	
	/**
	 * Handler for the parameters {@link Concurrency#expectMinimumSuccessRuns()}
	 * and {@link Concurrency#expectMaximumSuccessRuns()).
	 * 
	 * <small>Notifier works like an listener without an interface, yet.</small>
	 */
	private ExpectSuccessRunsHandler expectSuccessRunsHandler;
	
	/**
	 * Thread handling via concurrency package.
	 */
	private ExecutorService executorService;
	
	/**
	 * Cached junit class {@link Concurrency} annotation configuration.
	 */
	private ConcurrencyConfiguration configuration;
	
	/**
	 * For delegating to {@link BlockJUnit4ClassRunner}.
	 * 
	 * @param klass
	 * @throws InitializationError
	 */
	public ConcurrencyJunitRunner(final Class<?> klass) throws InitializationError {
		super(klass);
		configuration = new ConcurrencyConfiguration(klass);
	}

	/**
	 * Override the run method for creating n threads, calling the original
	 * framework method m times and call all the handlers.
	 */
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		runnerNotifyHandler = new RunnerNotifyHandler(notifier, describeChild(method));
		expectSuccessRunsHandler = new ExpectSuccessRunsHandler(runnerNotifyHandler, method);
		expectAtLeastHandler = new ExpectAtLeastHandler(runnerNotifyHandler, method);
		
		// Ignore @Ignore methods.
		if (method.getAnnotation(Ignore.class) != null) {
			runnerNotifyHandler.fireTestIgnored();
			return;
		}
		
		// Run the all the tests.
		runnerNotifyHandler.fireTestStarted();
		List<Future<Void>> tasks = createTasks(method);
		try {
			waitForFinishing(tasks);
		} finally {
			cancelTasks(tasks);
			expectAtLeastHandler.fireTestFinished();
			expectSuccessRunsHandler.fireTestFinished();
			runnerNotifyHandler.fireTestFinished();
		}
	}

	/**
	 * Create a new {@link #executorService} instance with a fixed number of
	 * parallel threads and a task list with n tasks. Return a list of Future
	 * for waiting for the task results.
	 * 
	 * @param method
	 * @return
	 */
	private List<Future<Void>> createTasks(FrameworkMethod method) {
		int times = configuration.getMethodTimes(method.getMethod());
		int threads = configuration.getMethodThreads(method.getMethod());
		
//		System.out.println(
//				"run ConcurrencyJunitTest " + method.getMethod().getDeclaringClass() +
//				"   " + method.getMethod().getName() +
//				"   " + times + " times in " + threads + " parallel threads!");
		
		executorService = Executors.newFixedThreadPool(threads);
		
		List<Future<Void>> tasks = new ArrayList<Future<Void>>(times);
		Callable<Void> callable = new MethodCallable(method);
		
		for (int i = 0; i < times; i++) {
			tasks.add(executorService.submit(callable));
		}
		
		return tasks;
	}
	
	/**
	 * Wait that all tasks are finished and call the handlers.
	 * 
	 * @param tasks
	 */
	private void waitForFinishing(List<Future<Void>> tasks) {
		for (Future<Void> future : tasks) {
			try {
				future.get();
				expectSuccessRunsHandler.handleSuccess();
			} catch (Throwable e) {
				expectAtLeastHandler.handleException(e);
			}
		}
	}
	
	/**
	 * Cancel and shutdown all the unfinished tasks.
	 * 
	 * @param tasks
	 */
	private void cancelTasks(List<Future<Void>> tasks) {
		for (Future<Void> future : tasks) {
			future.cancel(true);
		}
		executorService.shutdownNow();
	}
	
	/**
	 * <p>Wraps the call <code>methodBlock(method).evaluate()</code> into the
	 * {@link Callable} interface for the thread helping methods of java
	 * concurrency helper {@link ExecutorService}.</p>
	 * 
	 * <p>Calls methodBlock for each run to create anytime a new instance
	 * and call setUp (@Before) and tearDown (@After) correctly.</p>
	 */
	private class MethodCallable implements Callable<Void> {
		private FrameworkMethod method;

		private MethodCallable(FrameworkMethod method) {
			this.method = method;
		}

		public Void call() throws ExecutionException {
			try {
				methodBlock(method).evaluate();
				return null;
			} catch (Throwable e) {
				throw new ExecutionException(e);
			}
		}
	}
}
