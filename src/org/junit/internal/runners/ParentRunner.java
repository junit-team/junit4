package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.internal.runners.links.IgnoreViolatedAssumptions;
import org.junit.internal.runners.links.RunAfters;
import org.junit.internal.runners.links.RunBefores;
import org.junit.internal.runners.links.Statement;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.internal.runners.model.InitializationError;
import org.junit.internal.runners.model.TestClass;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

public abstract class ParentRunner<T> extends Runner {
	protected TestClass fTestClass;

	public ParentRunner(Class<?> testClass) {
		fTestClass = new TestClass(testClass);
	}

	protected abstract List<T> getChildren();
	
	protected abstract Description describeChild(T child);
	
	protected abstract Statement classBlock(RunNotifier notifier);
	
	@Override
	public void run(final RunNotifier notifier) {
		EachTestNotifier testNotifier= new EachTestNotifier(notifier,
				getDescription());
		try {
			Statement statement= new RunBefores(classBlock(notifier), fTestClass, null);
			statement= new IgnoreViolatedAssumptions(statement);
			statement= new RunAfters(statement, fTestClass, null);
			statement.evaluate();
		} catch (StoppedByUserException e) {
			throw e;
		} catch (Throwable e) {
			testNotifier.addFailure(e);
		}
	}

	@Override
	public Description getDescription() {
		Description description= Description.createSuiteDescription(getName(), classAnnotations());
		for (T child : getChildren())
			description.addChild(describeChild(child));
		return description;
	}

	protected TestClass getTestClass() {
		return fTestClass;
	}

	private Annotation[] classAnnotations() {
		return fTestClass.getJavaClass().getAnnotations();
	}

	protected String getName() {
		return fTestClass.getName();
	}

	// TODO: (Nov 14, 2007 11:04:54 AM) sort methods

	protected void assertValid(List<Throwable> errors) throws InitializationError {
		if (!errors.isEmpty())
			throw new InitializationError(errors);
	}
}