package org.junit.runner;

import java.util.Comparator;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.requests.ClassRequest;
import org.junit.internal.requests.FilterRequest;
import org.junit.internal.requests.SortingRequest;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

/**
 * <p>A <code>Request</code> is an abstract description of tests to be run. Older versions of 
 * JUnit did not need such a concept--tests to be run were described either by classes containing
 * tests or a tree of {@link  org.junit.Test}s. However, we want to support filtering and sorting,
 * so we need a more abstract specification than the tests themselves and a richer
 * specification than just the classes.</p>
 * 
 * <p>The flow when JUnit runs tests is that a <code>Request</code> specifies some tests to be run ->
 * a {@link org.junit.runner.Runner} is created for each class implied by the <code>Request</code> -> 
 * the {@link org.junit.runner.Runner} returns a detailed {@link org.junit.runner.Description} 
 * which is a tree structure of the tests to be run.</p>
 */
public abstract class Request {
	/**
	 * Create a <code>Request</code> that, when processed, will run a single test.
	 * This is done by filtering out all other tests. This method is used to support rerunning
	 * single tests.
	 * @param clazz the class of the test
	 * @param methodName the name of the test
	 * @return a <code>Request</code> that will cause a single test be run
	 */
	public static Request method(Class<?> clazz, String methodName) {
		Description method= Description.createTestDescription(clazz, methodName);
		return Request.aClass(clazz).filterWith(method);
	}

	/**
	 * Create a <code>Request</code> that, when processed, will run all the tests
	 * in a class. The odd name is necessary because <code>class</code> is a reserved word.
	 * @param clazz the class containing the tests
	 * @return a <code>Request</code> that will cause all tests in the class to be run
	 */
	public static Request aClass(Class<?> clazz) {
		return new ClassRequest(clazz);
	}

	/**
	 * Create a <code>Request</code> that, when processed, will run all the tests
	 * in a class. If the class has a suite() method, it will be ignored.
	 * @param clazz the class containing the tests
	 * @return a <code>Request</code> that will cause all tests in the class to be run
	 */
	public static Request classWithoutSuiteMethod(Class<?> clazz) {
		return new ClassRequest(clazz, false);
	}

	/**
	 * Create a <code>Request</code> that, when processed, will run all the tests
	 * in a set of classes.
	 * @param classes the classes containing the tests
	 * @return a <code>Request</code> that will cause all tests in the classes to be run
	 */
	public static Request classes(Class<?>... classes) {
		try {
			return runner(new Suite(new AllDefaultPossibilitiesBuilder(true), classes));
		} catch (InitializationError e) {
			throw new RuntimeException(
					"Bug in saff's brain: Suite constructor, called as above, should always complete");
		}
	}

	/**
	 * Not used within JUnit.  Clients should simply instantiate ErrorReportingRunner themselves
	 */
	@Deprecated	
	public static Request errorReport(Class<?> klass, Throwable cause) {
		return runner(new ErrorReportingRunner(klass, cause));
	}

	/**
	 * @param runner the runner to return
	 * @return a <code>Request</code> that will run the given runner when invoked
	 */
	public static Request runner(final Runner runner) {
		return new Request(){
			@Override
			public Runner getRunner() {
				return runner;
			}		
		};
	}

	/**
	 * Returns a {@link Runner} for this Request
	 * @return corresponding {@link Runner} for this Request
	 */
	public abstract Runner getRunner();

	/**
	 * Returns a Request that only contains those tests that should run when
	 * <code>filter</code> is applied
	 * @param filter The {@link Filter} to apply to this Request
	 * @return the filtered Request
	 */
	public Request filterWith(Filter filter) {
		return new FilterRequest(this, filter);
	}

	/**
	 * Returns a Request that only runs contains tests whose {@link Description}
	 * equals <code>desiredDescription</code>
	 * @param desiredDescription {@link Description} of those tests that should be run
	 * @return the filtered Request
	 */
	public Request filterWith(final Description desiredDescription) {
		return filterWith(new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				if (description.isTest())
					return desiredDescription.equals(description);
				
				// explicitly check if any children want to run
				for (Description each : description.getChildren())
					if (shouldRun(each))
						return true;
				return false;					
			}

			@Override
			public String describe() {
				return String.format("Method %s", desiredDescription.getDisplayName());
			}
		});
	}

	/**
	 * Returns a Request whose Tests can be run in a certain order, defined by 
	 * <code>comparator</code>
	 * 
	 * For example, here is code to run a test suite in alphabetical order:
	 * 
	 * <pre>
	private static Comparator<Description> forward() {
		return new Comparator<Description>() {
			public int compare(Description o1, Description o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		};
	}
	
	public static main() {
		new JUnitCore().run(Request.aClass(AllTests.class).sortWith(forward()));
	}
	 * </pre>
	 * 
	 * @param comparator definition of the order of the tests in this Request
	 * @return a Request with ordered Tests
	 */
	public Request sortWith(Comparator<Description> comparator) {
		return new SortingRequest(this, comparator);
	}
}
