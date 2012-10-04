package junit.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class JUnit4TestAdapterCache extends HashMap<Description, Test> {
    private static final long serialVersionUID = 1L;
    private static final JUnit4TestAdapterCache fInstance = new JUnit4TestAdapterCache();

    public static JUnit4TestAdapterCache getDefault() {
        return fInstance;
    }

    public Test asTest(Description description) {
        if (description.isSuite()) {
            return createTest(description);
        } else {
            if (!containsKey(description)) {
                put(description, createTest(description));
            }
            return get(description);
        }
    }

    Test createTest(Description description) {
        if (description.isTest()) {
            return new JUnit4TestCaseFacade(description);
        } else {
            TestSuite suite = new TestSuite(description.getDisplayName());
            for (Description child : description.getChildren()) {
                suite.addTest(asTest(child));
            }
            return suite;
        }
    }

    public RunNotifier getNotifier(final TestResult result, final JUnit4TestAdapter adapter) {
        RunNotifier notifier = new RunNotifier();
        notifier.addListener(new RunListener() {
            @Override
            public void testFailure(Failure failure) throws Exception {
                result.addError(asTest(failure.getDescription()), failure.getException());
            }

            @Override
            public void testFinished(Description description) throws Exception {
                result.endTest(asTest(description));
            }

            @Override
            public void testStarted(Description description) throws Exception {
                result.startTest(asTest(description));
            }
        });
        return notifier;
    }

    public List<Test> asTestList(Description description) {
        if (description.isTest()) {
            return Arrays.asList(asTest(description));
        } else {
            List<Test> returnThis = new ArrayList<Test>();
            for (Description child : description.getChildren()) {
                returnThis.add(asTest(child));
            }
            return returnThis;
        }
    }

}