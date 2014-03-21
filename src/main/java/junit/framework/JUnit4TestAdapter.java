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
    private final Class<?> newTestClass;

    private final Runner runner;

    private final JUnit4TestAdapterCache cache;

    public JUnit4TestAdapter(Class<?> newTestClass) {
        this(newTestClass, JUnit4TestAdapterCache.getDefault());
    }

    public JUnit4TestAdapter(final Class<?> newTestClass, JUnit4TestAdapterCache cache) {
        this.cache = cache;
        this.newTestClass = newTestClass;
        runner = Request.classWithoutSuiteMethod(newTestClass).getRunner();
    }

    public int countTestCases() {
        return runner.testCount();
    }

    public void run(TestResult result) {
        runner.run(cache.getNotifier(result, this));
    }

    // reflective interface for Eclipse
    public List<Test> getTests() {
        return cache.asTestList(getDescription());
    }

    // reflective interface for Eclipse
    public Class<?> getTestClass() {
        return newTestClass;
    }

    public Description getDescription() {
        Description description = runner.getDescription();
        return removeIgnored(description);
    }

    private Description removeIgnored(Description description) {
        if (isIgnored(description)) {
            return Description.EMPTY;
        }
        Description result = description.childlessCopy();
        for (Description each : description.getChildren()) {
            Description child = removeIgnored(each);
            if (!child.isEmpty()) {
                result.addChild(child);
            }
        }
        return result;
    }

    private boolean isIgnored(Description description) {
        return description.getAnnotation(Ignore.class) != null;
    }

    @Override
    public String toString() {
        return newTestClass.getName();
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        filter.apply(runner);
    }

    public void sort(Sorter sorter) {
        sorter.apply(runner);
    }
}