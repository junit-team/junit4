package org.junit.experimental.max;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class MaxHistory implements Serializable {
	private static final long serialVersionUID= 1L;


	public static MaxHistory forFolder(File storedResults) {
		try {
			if (storedResults.exists())
				return readHistory(storedResults);
		} catch (CouldNotReadCoreException e) {
			e.printStackTrace();
			storedResults.delete();
		}
		return new MaxHistory(storedResults);
	}

	private static MaxHistory readHistory(File storedResults) throws CouldNotReadCoreException {
		// TODO: rule of three
		// TODO: Really?
		ObjectInputStream stream;
		try {
			stream= new ObjectInputStream(new FileInputStream(storedResults));
		} catch (IOException e) {
			throw new CouldNotReadCoreException(e);
		}
		try {
			return (MaxHistory) stream.readObject();
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
	
	public final Map<String, Long> fDurations= new HashMap<String, Long>();

	public final Map<String, Long> fFailureTimestamps= new HashMap<String, Long>();

	public final File fFolder;

	public MaxHistory(File storedResults) {
		fFolder= storedResults;
	}

	public File getFile() {
		return fFolder;
	}

	public void save() throws IOException {
		ObjectOutputStream stream= new ObjectOutputStream(new FileOutputStream(
				fFolder));
		stream.writeObject(this);
		stream.close();
	}

	Long getFailureTimestamp(Description key) {
		return fFailureTimestamps.get(key.toString());
	}

	void putTestFailureTimestamp(Description key, long end) {
		fFailureTimestamps.put(key.toString(), end);
	}

	boolean isNewTest(Description key) {
		return !fDurations.containsKey(key.toString());
	}

	Long getTestDuration(Description key) {
		return fDurations.get(key.toString());
	}

	void putTestDuration(Description description, long duration) {
		fDurations.put(description.toString(), duration);
	}

	private final class RememberingListener extends RunListener {
		private long overallStart= System.currentTimeMillis();

		private Map<Description, Long> starts= new HashMap<Description, Long>();

		@Override
		public void testStarted(Description description) throws Exception {
			starts.put(description, System.nanoTime()); // Get most accurate
														// possible time
		}

		@Override
		public void testFinished(Description description) throws Exception {
			long end= System.nanoTime();
			long start= starts.get(description);
			putTestDuration(description, end - start);
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			putTestFailureTimestamp(failure.getDescription(), overallStart);
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


	public RememberingListener listener() {
		return new RememberingListener();
	}

	// TODO (Feb 23, 2009 10:41:36 PM): V
	public Comparator<Description> testComparator() {
		return new TestComparator();
	}
}