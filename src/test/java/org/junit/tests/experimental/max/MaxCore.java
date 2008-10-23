package org.junit.tests.experimental.max;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class MaxCore {
	protected Map<Description, Long> fDurations= new HashMap<Description, Long>();
	protected Map<Description, Long> fFailures= new HashMap<Description, Long>();
	
	public void run(Request request) {
		JUnitCore core= new JUnitCore();
		core.addListener(new RunListener() {
			private Map<Description, Long> starts= new HashMap<Description, Long>();

			@Override
			public void testStarted(Description description) throws Exception {
				starts.put(description, System.nanoTime()); // Get most accurate possible time
			}
			
			@Override
			public void testFinished(Description description) throws Exception {
				long end= System.nanoTime();
				long start= starts.get(description);
				fDurations.put(description, end - start);
			}
			
			@Override
			public void testFailure(Failure failure) throws Exception {
				long end= System.currentTimeMillis(); // This needs to be comparable across tests
				fFailures.put(failure.getDescription(), end);
			}
		});
		core.run(request);
	}

	public List<Description> sort(Request request) {
		List<Description> tests= findLeaves(request);
		Collections.sort(tests, new TestComparator());
		return tests;
	}

	private class TestComparator implements Comparator<Description> {
		public int compare(Description o1, Description o2) {
			// Always prefer new tests
			if (isNew(o1))
				return -1;
			if (isNew(o2))
				return 1;
			// Then most recently failed first
			int result= getFailure(o2).compareTo(getFailure(o1)); 
			return result != 0
				? result
				// Then shorter tests first
				: fDurations.get(o1).compareTo(fDurations.get(o2));
		}
	
		private boolean isNew(Description o1) {
			return ! fDurations.containsKey(o1);
		}
	
		private Long getFailure(Description o1) {
			Long result= fFailures.get(o1);
			if (result == null) result= 0L; // 0 = "never failed (that I know about)"
			return result;
		}
	}

	private List<Description> findLeaves(Request request) {
		List<Description> results= new ArrayList<Description>();
		findLeaves(request.getRunner().getDescription(), results);
		return results;
	}
	
	private void findLeaves(Description description, List<Description> results) {
		if (description.getChildren().isEmpty())
			results.add(description);
		else
			for (Description each : description.getChildren())
				findLeaves(each, results);
	}
}
