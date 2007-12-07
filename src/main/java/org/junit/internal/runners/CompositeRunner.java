package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.RunNotifier;

public class CompositeRunner extends ParentRunner<Runner> implements Filterable, Sortable {
	private final List<Runner> fRunners= new ArrayList<Runner>();
	private final String fName;
	
	public CompositeRunner(String name) {
		this(null, name);
	}
	
	public CompositeRunner(Class<?> type, String name) {
		super(type);
		fName = name;
	}
	
	public void add(Runner runner) {
		fRunners.add(runner);
	}

	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}
	
	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner each, final RunNotifier notifier) {
		each.run(notifier);
	}
	
	@Override
	protected String getName() {
		return fName;
	}
	
	@Override
	protected Annotation[] classAnnotations() {
		return new Annotation[0];
	}

	
	@Override
	protected Runner filterChild(Runner child, Filter filter) throws NoTestsRemainException {
		Filter.apply(filter, child);
		return child;
	}
	
	@Override
	protected Runner sortChild(Runner child, Sorter sorter) {
		Sorter.apply(sorter, child);
		return child;
	}
}
