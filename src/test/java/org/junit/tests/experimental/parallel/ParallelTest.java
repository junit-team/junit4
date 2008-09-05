package org.junit.tests.experimental.parallel;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;


public class ParallelTest {
	public static class ParallelRunner extends BlockJUnit4ClassRunner {
		private ExecutorService fService= Executors.newCachedThreadPool();
		private List<Future<Object>> fResults= new ArrayList<Future<Object>>();

		public ParallelRunner(Class<?> klass) throws InitializationError {
			super(klass);
		}
		
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
					each.get(2000, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
					e.printStackTrace();
				} 
		}
	}

	@RunWith(ParallelRunner.class)
	public static class Example {
		@Test public void one() throws InterruptedException {
			Thread.sleep(1000);
		}
		@Test public void two() throws InterruptedException {
			Thread.sleep(1000);
		}
	}
	
	@Test(timeout=1500) public void testsRunInParallel() {
		long start= System.currentTimeMillis();
		assertThat(PrintableResult.testResult(Example.class), ResultMatchers.isSuccessful());
		long end= System.currentTimeMillis();
		assertThat(end - start, greaterThan(1000));
	}

	private Matcher<Long> greaterThan(final long l) {
		return new TypeSafeMatcher<Long>() {
			@Override
			public boolean matchesSafely(Long item) {
				return item > l;
			}

			public void describeTo(Description description) {
				description.appendText("greater than " + l);
			}
		};
	}
}
