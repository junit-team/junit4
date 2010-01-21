package junit.framework;

import java.util.List;

import org.junit.Ignore;
import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;

public class JUnit4TestAdapter implements Test, Filterable, Sortable, Describable {
	private final Class<?> fNewTestClass;

	private final Runner fRunner;

	private final JUnit4TestAdapterCache fCache;

	public JUnit4TestAdapter(Class<?> newTestClass) {
		this(newTestClass, JUnit4TestAdapterCache.getDefault());
	}

	public JUnit4TestAdapter(final Class<?> newTestClass,
			JUnit4TestAdapterCache cache) {
		fCache = cache;
		fNewTestClass = newTestClass;
		fRunner = Request.classWithoutSuiteMethod(newTestClass).filterWith(removeIgnored()).getRunner();
	}

	public int countTestCases() {
		return fRunner.testCount();
	}

	public void run(TestResult result) {
		fRunner.run(fCache.getNotifier(result, this));
	}

	// reflective interface for Eclipse
	public List<Test> getTests() {
		return fCache.asTestList(fRunner.getPlan());
	}

	// reflective interface for Eclipse
	public Class<?> getTestClass() {
		return fNewTestClass;
	}
	
	public Description getDescription() {
		return fRunner.getDescription();
	}

	private Filter removeIgnored() {
		return new Filter() {			
			@Override
			public boolean shouldRun(Description description) {
				return !isIgnored(description);
			}
			
			@Override
			public String describe() {
				return "not ignored";
			}
		};
	}

	private boolean isIgnored(Description description) {
		return description.getAnnotation(Ignore.class) != null;
	}

	@Override
	public String toString() {
		return fNewTestClass.getName();
	}

	public void filter(Filter filter) throws NoTestsRemainException {
		filter.apply(fRunner);
	}

	public void sort(Sorter sorter) {
		sorter.apply(fRunner);
	}
}