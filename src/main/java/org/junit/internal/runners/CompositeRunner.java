package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.notification.RunNotifier;

// TODO: (Dec 10, 2007 1:41:20 PM) Can this go away?

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
}
