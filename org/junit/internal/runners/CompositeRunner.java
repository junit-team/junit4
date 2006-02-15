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

public class CompositeRunner extends Runner implements Filterable, Sortable {
	private final List<Runner> fRunners= new ArrayList<Runner>();
	private final String fName;
	
	public CompositeRunner(String name) {
		fName= name;
	}
	
	@Override
	public void run(RunNotifier notifier) {
		for (Runner each : fRunners)
			each.run(notifier);
	}

	@Override
	public Description getDescription() {
		Description spec= Description.createSuiteDescription(fName);
		for (Runner runner : fRunners) {
			spec.addChild(runner.getDescription());
		}
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
		for (Iterator iter= fRunners.iterator(); iter.hasNext();) {
			Runner runner= (Runner) iter.next();
			if (filter.shouldRun(runner.getDescription())) {
				filter.apply(runner);
			} else {
				iter.remove();
			}
		}
	}

	protected String getName() {
		return fName;
	}

	public void sort(final Sorter sorter) {
		Collections.sort(fRunners, new Comparator<Runner>() {
			public int compare(Runner o1, Runner o2) {
				return sorter.compare(o1.getDescription(), o2.getDescription());
			}
		});
		for (Runner each : fRunners) {
			sorter.apply(each);
		}
	}
}
