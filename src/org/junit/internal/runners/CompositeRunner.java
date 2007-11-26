package org.junit.internal.runners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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

	// TODO: (Nov 7, 2007 1:48:55 PM) absorb into ParentRunner

	public CompositeRunner(Class<?> type) {
		// TODO: (Nov 7, 2007 1:59:24 PM) null is bad

		this(type, null);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(fName);
		for (Runner runner : fRunners)
			spec.addChild(runner.getDescription());
		return spec;
	}

	public List<Runner> getRunners() {
		return fRunners;
	}

	public void addAll(List<? extends Runner> runners) {
		fRunners.addAll(runners);
	}

	public void add(Runner runner) {
		fRunners.add(runner);
	}
	
	public void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<Runner> iter= fRunners.iterator(); iter.hasNext();) {
			Runner runner= iter.next();
			
			// if filter(parent) == false, tree is pruned			
			if (filter.shouldRun(runner.getDescription()))
				filter.apply(runner);
			else
				iter.remove();
		}
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fRunners, new Comparator<Runner>() {
			public int compare(Runner o1, Runner o2) {
				return sorter.compare(o1.getDescription(), o2.getDescription());
			}
		});
		for (Runner each : fRunners)
			sorter.apply(each);
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}

	@Override
	protected void runChild(Runner each, final RunNotifier notifier) {
		each.run(notifier);
	}
}
