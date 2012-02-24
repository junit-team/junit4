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
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Stores a subset of the history of each test:
 * <ul>
 * <li>Last failure timestamp
 * <li>Duration of last execution
 * </ul>
 */
public class MaxHistory implements Serializable {
	private static final long serialVersionUID= 1L;

	/**
	 * Loads a {@link MaxHistory} from {@code file}, or generates a new one that
	 * will be saved to {@code file}.
	 */
	public static MaxHistory forFolder(File file) {
		if (file.exists())
			try {
				return readHistory(file);
			} catch (CouldNotReadCoreException e) {
				e.printStackTrace();
				file.delete();
			}
		return new MaxHistory(file);
	}

	private static MaxHistory readHistory(File storedResults)
			throws CouldNotReadCoreException {
		try {
			FileInputStream file= new FileInputStream(storedResults);
			try {
				ObjectInputStream stream= new ObjectInputStream(file);
				try {
					return (MaxHistory) stream.readObject();
				} finally {
					stream.close();
				}
			} finally {
				file.close();
			}
		} catch (Exception e) {
			throw new CouldNotReadCoreException(e);
		}
	}

	private final Map<String, Long> fDurations= new HashMap<String, Long>();

	private final Map<String, Long> fFailureTimestamps= new HashMap<String, Long>();

	private final File fHistoryStore;

	private MaxHistory(File storedResults) {
		fHistoryStore= storedResults;
	}

	private void save() throws IOException {
		ObjectOutputStream stream= new ObjectOutputStream(new FileOutputStream(
				fHistoryStore));
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

		@Override
		public void testRunFinished(Result result) throws Exception {
			save();
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
			return result != 0 ? result
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

	/**
	 * @return a listener that will update this history based on the test
	 *         results reported.
	 */
	public RunListener listener() {
		return new RememberingListener();
	}

	/**
	 * @return a comparator that ranks tests based on the JUnit Max sorting
	 *         rules, as described in the {@link MaxCore} class comment.
	 */
	public Comparator<Description> testComparator() {
		return new TestComparator();
	}
}
