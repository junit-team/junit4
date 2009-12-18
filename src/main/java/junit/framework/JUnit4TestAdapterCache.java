/**
 * 
 */
package junit.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Plan;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

public class JUnit4TestAdapterCache extends HashMap<Description, Test> {
	private static final long serialVersionUID = 1L;
	private static final JUnit4TestAdapterCache fInstance = new JUnit4TestAdapterCache();

	public static JUnit4TestAdapterCache getDefault() {
		return fInstance;
	}
	
	// TODO: rename var
	public Test asTest(Plan plan) {
		if (plan.isSuite())
			return createTest(plan);
		else
			return asSingleTest(plan.getDescription());
	}

	private Test asSingleTest(Description description) {
		if (!containsKey(description))
			put(description, createSingleTest(description));
		return get(description);
	}

	Test createTest(Plan plan) {
		Description description= plan.getDescription();
		if (plan.isTest())
			return createSingleTest(description);
		else {
			TestSuite suite = new TestSuite(description.getDisplayName());
			for (Plan child : plan.getChildren())
				suite.addTest(asTest(child));
			return suite;
		}
	}

	private JUnit4TestCaseFacade createSingleTest(Description description) {
		return new JUnit4TestCaseFacade(description);
	}

	public RunNotifier getNotifier(final TestResult result,
			final JUnit4TestAdapter adapter) {
		RunNotifier notifier = new RunNotifier();
		notifier.addListener(new RunListener() {
			@Override
			public void testFailure(Failure failure) throws Exception {
				result.addError(asSingleTest(failure.getDescription()), failure.getException());
			}

			@Override
			public void testFinished(Description description)
					throws Exception {
				result.endTest(asSingleTest(description));
			}

			@Override
			public void testStarted(Description description)
					throws Exception {
				// TODO: this needs to test that the test is created if not seen before
				result.startTest(asSingleTest(description));
			}
		});
		return notifier;
	}

	public List<Test> asTestList(Plan plan) {
		if (plan.isTest())
			return Arrays.asList(asTest(plan));
		else {
			List<Test> returnThis = new ArrayList<Test>();
			for (Plan child : plan.getChildren()) {
				returnThis.add(asTest(child));
			}
			return returnThis;
		}
	}

}