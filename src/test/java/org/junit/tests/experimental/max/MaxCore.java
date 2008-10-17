package org.junit.tests.experimental.max;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class MaxCore {
	private List<Failure> fFailures= new ArrayList<Failure>();
	// private Map<Description, Long> fTimes= new HashMap<Description, Long>();
	
	public List<Odds> getSpreads(Request request) {
		List<Description> leaves= findLeaves(request.getRunner().getDescription());
		Collections.sort(leaves, failureComparator());
		List<Odds> results= new ArrayList<Odds>();
		for (Description leaf : leaves)
			results.add(new Odds(leaf, 0.0));
		return results;
	}

	private Comparator<? super Description> failureComparator() { // TODO also take runtime into account
		return new Comparator<Description>() {
			public int compare(Description o1, Description o2) {
				// TODO: wrong (Failures are not Descriptions)
				return fFailures.contains(o1) ? -1 : 1;
			}
		};
	}

	private List<Description> findLeaves(Description description) {
		List<Description> results= new ArrayList<Description>();
		findLeaves(description, results);
		return results;
	}

	private void findLeaves(Description description, List<Description> results) {
		if (description.getChildren().isEmpty())
			results.add(description);
		else
			for (Description each : description.getChildren())
				findLeaves(each, results);
	}

	public void run(Request request) {
		JUnitCore core= new JUnitCore();
		core.addListener(new RunListener() {
			@Override
			public void testStarted(Description description) throws Exception {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void testFinished(Description description) throws Exception {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void testFailure(Failure failure) throws Exception {
				throw new UnsupportedOperationException();
			}
		});
		Result result= core.run(request);
		fFailures= result.getFailures();
	}

}