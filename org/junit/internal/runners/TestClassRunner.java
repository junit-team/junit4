package org.junit.internal.runners;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.Failure;

public class TestClassRunner extends Runner implements Filterable, Sortable {
	protected final Runner fEnclosedRunner;
	private final Class<?> fTestClass;

	public TestClassRunner(Class<?> klass) throws InitializationError {
		this(klass, new TestClassMethodsRunner(klass));
	}
	
	public TestClassRunner(Class<?> klass, Runner runner) throws InitializationError {
		fTestClass= klass;
		fEnclosedRunner= runner;
		MethodValidator methodValidator= new MethodValidator(klass);
		validate(methodValidator);
		methodValidator.assertValid();
	}

	// TODO: this is parallel to passed-in runner
	protected void validate(MethodValidator methodValidator) {
		methodValidator.validateAllMethods();
	}

	@Override
	public void run(final RunNotifier notifier) {
		BeforeAndAfterRunner runner = new BeforeAndAfterRunner(getTestClass(),
				BeforeClass.class, AfterClass.class, null) {		
			@Override
			protected void runUnprotected() {
				fEnclosedRunner.run(notifier);
			}
		
			// TODO: looks very similar to other method of BeforeAfter, now
			@Override
			protected void addFailure(Throwable targetException) {
				notifier.fireTestFailure(new Failure(getDescription(), targetException));
			}
		};

		runner.runProtected();
	}

	@Override
	public Description getDescription() {
		return fEnclosedRunner.getDescription();
	}
	
	// TODO: good behavior when createTest fails
	
	// TODO: dup?
	public void filter(Filter filter) throws NoTestsRemainException {
		filter.apply(fEnclosedRunner);
	}

	public void sort(Sorter sorter) {
		sorter.apply(fEnclosedRunner);
	}

	protected Class<?> getTestClass() {
		return fTestClass;
	}
}
