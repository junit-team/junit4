package org.junit.experimental.max;

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
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

// TODO (Nov 18, 2008 1:40:42 PM): Is this doing too much?
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
			throw new CouldNotReadCoreException(e); //TODO think about what we can do better here
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

	public Result run(Request request) {
		return run(request, new JUnitCore());
	}

	public Result run(Request request, JUnitCore core) {
		core.addListener(new RememberingListener());
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
	
	public String getFolder() {
		return fFolder;
	}

	public void forget() {
		new File(fFolder).delete();
	}

	private Request sortRequest(Request request) {
		if (request instanceof SortingRequest) // We'll pay big karma points for this
			return request;
		List<Description> leaves= findLeaves(request);
		Collections.sort(leaves, new TestComparator());
		return constructLeafRequest(leaves);
	}

	private Request constructLeafRequest(List<Description> leaves) {
		final List<Runner> runners = new ArrayList<Runner>();
		for (Description each : leaves)
			runners.add(buildRunner(each));
		return new Request() {
			@Override
			public Runner getRunner() {
				try {
					return new Suite((Class<?>)null, runners) {};
				} catch (InitializationError e) {
					return new ErrorReportingRunner(null, e);
				}
			}
		};
	}

	private Runner buildRunner(Description each) {
		if (each.toString().equals("TestSuite with 0 tests"))
			try {
				// TODO (Nov 18, 2008 2:18:28 PM): move to Suite
				return new Suite(null, new Class<?>[0]);
			} catch (InitializationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		Class<?> type= each.getTestClass();
		if (type == null)
			// TODO (Nov 18, 2008 2:04:09 PM): add a check if building a runner is possible
			throw new RuntimeException("Can't build a runner from description [" + each + "]");
		return Request.method(type, each.getMethodName()).getRunner();
	}

	private void save() throws FileNotFoundException, IOException {
		ObjectOutputStream stream= new ObjectOutputStream(new FileOutputStream(fFolder + ".ser"));
		stream.writeObject(this);
		stream.close();
	}

	public List<Description> sortedLeavesForTest(Request request) {
		return findLeaves(sortRequest(request));
	}

	private final class RememberingListener extends RunListener {
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
			if (result == null) 
				return 0L; // 0 = "never failed (that I know about)"
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

	private void putTestFailureTimestamp(Description key, long end) {
		fFailureTimestamps.put(key.toString(), end);
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
}

