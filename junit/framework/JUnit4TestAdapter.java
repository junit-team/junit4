package junit.framework;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;

public class JUnit4TestAdapter implements Test {
	private final Class<?> fNewTestClass;

	private Runner fRunner;

	private JUnit4TestAdapterCache fCache;

	public JUnit4TestAdapter(Class<?> newTestClass) {
		this(newTestClass, JUnit4TestAdapterCache.getDefault());
	}

	public JUnit4TestAdapter(final Class<?> newTestClass,
			JUnit4TestAdapterCache cache) {
		fCache = cache;
		fNewTestClass = newTestClass;
		fRunner = Request.aClass(newTestClass).getRunner();
	}

	public int countTestCases() {
		return fRunner.testCount();
	}

	public void run(TestResult result) {
		fRunner.run(fCache.getNotifier(result, this));
	}

	// reflective interface for Eclipse
	public List<Test> getTests() {
		return fCache.asTestList(getDescription());
	}

	// reflective interface for Eclipse
	public Class getTestClass() {
		return fNewTestClass;
	}
	
	public Description getDescription() {
		return fRunner.getDescription();
	}

	@Override
	public String toString() {
		return fNewTestClass.getName();
	}
}