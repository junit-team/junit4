package org.junit.runner;

import java.util.Comparator;

import org.junit.internal.requests.ClassRequest;
import org.junit.internal.requests.ClassesRequest;
import org.junit.internal.requests.ErrorReportingRequest;
import org.junit.internal.requests.FilterRequest;
import org.junit.internal.requests.SortingRequest;
import org.junit.runner.manipulation.Filter;

/**
 * A <code>Request</code> is an abstract description of tests to be run. Older versions of 
 * JUnit did not need such a concept--tests to be run were described either by classes containing
 * tests or a tree of <code>Tests</code>. However, we want to support filtering and sorting,
 * so we need a more abstract specification than the tests themselves and a richer
 * specification than just the classes.
 * <p>
 * The flow when JUnit runs tests is that a <code>Request</code> specifies some tests to be run ->
 * a <code>Runner</code> is created for each class implied by the <code>Request</code> -> the <code>Runner</code>
 * returns a detailed <code>Description</code> which is a tree structure of the tests to be run.
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
	 * in a set of classes.
	 * @param collectionName a name to identify this suite of tests
	 * @param classes the classes containing the tests
	 * @return a <code>Request</code> that will cause all tests in the classes to be run
	 */
	public static Request classes(String collectionName, Class... classes) {
		return new ClassesRequest(collectionName, classes);
	}

	public static Request errorReport(Class<?> klass, Throwable cause) {
		return new ErrorReportingRequest(klass, cause);
	}

	public abstract Runner getRunner();

	public Request filterWith(Filter filter) {
		return new FilterRequest(this, filter);
	}

	public Request filterWith(final Description desiredDescription) {
		return filterWith(new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				// TODO: test for equality even if we have children?
				if (description.isTest())
					return desiredDescription.equals(description);
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

	public Request sortWith(Comparator<Description> comparator) {
		return new SortingRequest(this, comparator);
	}
}
