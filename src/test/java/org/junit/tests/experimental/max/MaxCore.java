package org.junit.tests.experimental.max;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.internal.requests.SortingRequest;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class MaxCore implements Serializable {
	private static final long serialVersionUID= 1L;

	public static MaxCore forFolder(String folder) throws CouldNotReadCoreException {
		if (new File(folder + ".ser").exists())
			return readCore(folder);
		return new MaxCore(folder);
	}

	private static MaxCore readCore(String folder) throws CouldNotReadCoreException {
		// TODO: rule of three
		// TODO: Really?
		ObjectInputStream stream;
		try {
			stream= new ObjectInputStream(new FileInputStream(folder + ".ser"));
		} catch (IOException e) {
			throw new CouldNotReadCoreException(e);
		}
		try {
			return (MaxCore) stream.readObject();
		} catch (Exception e) {
			throw new CouldNotReadCoreException(e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				throw new CouldNotReadCoreException(e);
			}
		}
	}

	public static MaxCore createFresh() {
		return new MaxCore();
	}

	protected Map<String, Long> fDurations= new HashMap<String, Long>();
	protected Map<String, Long> fFailureTimestamps= new HashMap<String, Long>();
	private final String fFolder;
	
	private MaxCore(String folder) {
		fFolder= folder;
	}

	private MaxCore() {
		// TODO: ensure fresh
		this("MaxCore");
	}

	public void run(Request request) {
		run(request, new JUnitCore());
	}

	public Result run(Request request, JUnitCore core) {
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
				putTestDuration(description, end - start);
			}

			@Override
			public void testFailure(Failure failure) throws Exception {
				long end= System.currentTimeMillis(); // This needs to be comparable across tests
				putTestFailureTimestamp(failure.getDescription(), end);
			}
		});
		try { 
			return core.run(sortRequest(request).getRunner());
		} finally {
			try {
				save();
			} catch (FileNotFoundException e) {
				// TODO
				e.printStackTrace();
			} catch (IOException e) {
				// TODO
				e.printStackTrace();
			}
		}
	}

	private Request sortRequest(Request request) {
		if (request instanceof SortingRequest) { // We'll pay big karma points for this
			return request;
		}
		return request.sortWith(new TestComparator());
	}

	private void save() throws FileNotFoundException, IOException {
		ObjectOutputStream stream= new ObjectOutputStream(new FileOutputStream(fFolder + ".ser"));
		stream.writeObject(this);
		stream.close();
	}

	public List<Description> sort(Request request) {
		List<Description> tests= findLeaves(request);
		Collections.sort(tests, new TestComparator());
		return tests;
	}

	private class TestComparator implements Comparator<Description> {
		public int compare(Description o1, Description o2) {
			// Always prefer new tests
			if (isNewTest(o1))
				return -1;
			if (isNewTest(o2))
				return 1;
			// Then most recently failed first
			int result= getFailure(o2).compareTo(getFailure(o1)); 
			return result != 0
				? result
				// Then shorter tests first
				: getTestDuration(o1).compareTo(getTestDuration(o2));
		}
	
		private Long getFailure(Description key) {
			Long result= getFailureTimestamp(key);
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
	
	private Long getFailureTimestamp(Description key) {
		return fFailureTimestamps.get(key.toString());
	}

	private boolean isNewTest(Description key) {
		return ! fDurations.containsKey(key.toString());
	}
	
	private Long getTestDuration(Description key) {
		return fDurations.get(key.toString());
	}
	
	private void putTestDuration(Description description, long duration) {
		fDurations.put(description.toString(), duration);
	}

	private void putTestFailureTimestamp(Description key, long end) {
		fFailureTimestamps.put(key.toString(), end);
	}
}

