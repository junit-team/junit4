package org.junit.runners;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParentRunnerWrapper<T> extends ParentRunner<T> {
	protected ParentRunnerWrapper(Class<?> testClass, ParentRunner<T> parentRunner)
			throws InitializationError {
		super(testClass);
		this.parentRunner = parentRunner;
	}

	private final ParentRunner<T> parentRunner;
	
	protected ParentRunner<T> getWrappedRunner() {
		return this.parentRunner;
	}

	@Override
	public int testCount() {
		return parentRunner.testCount();
	}

	@Override
	public int hashCode() {
		return parentRunner.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return parentRunner.equals(obj);
	}

	@Override
	public String toString() {
		return parentRunner.toString();
	}

	@Override
	public Description getDescription() {
		return parentRunner.getDescription();
	}

	@Override
	public void run(RunNotifier notifier) {
		parentRunner.run(notifier);
	}

	@Override
	public void filter(Filter filter) throws NoTestsRemainException {
		parentRunner.filter(filter);
	}

	@Override
	public void sort(Sorter sorter) {
		parentRunner.sort(sorter);
	}

	@Override
	public void setScheduler(RunnerScheduler scheduler) {
		parentRunner.setScheduler(scheduler);
	}

	@Override
	protected List<T> getChildren() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Description describeChild(T child) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void runChild(T child, RunNotifier notifier) {
		throw new UnsupportedOperationException();
	}
	
	
}
