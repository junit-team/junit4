package org.junit.runner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.runner.Version;
import org.junit.internal.JUnitSystem;
import org.junit.internal.RealSystem;
import org.junit.internal.TextListener;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * <code>JUnitCore</code> is a facade for running tests. It supports running JUnit 4 tests, 
 * JUnit 3.8.x tests, and mixtures. To run tests from the command line, run 
 * <code>java org.junit.runner.JUnitCore TestClass1 TestClass2 ...</code>.
 * For one-shot test runs, use the static method {@link #runClasses(Class[])}. 
 * If you want to add special listeners,
 * create an instance of {@link org.junit.runner.JUnitCore} first and use it to run the tests.
 * 
 * @see org.junit.runner.Result
 * @see org.junit.runner.notification.RunListener
 * @see org.junit.runner.Request
 * @since 4.0
 */
public class JUnitCore {
	/**
	 * Separates class name from method name, e.g. ClassName#MethodName
	 */
	private static final String SEPARATOR= "#";
	
	private final RunNotifier fNotifier= new RunNotifier();

	/**
	 * Run the tests contained in the classes named in the <code>args</code>.
	 * If all tests run successfully, exit with a status of 0. Otherwise exit with a status of 1.
	 * Write feedback while tests are running and write
	 * stack traces for all failed tests after the tests all complete.
	 * @param args names of classes in which to find tests to run
	 */
	public static void main(String... args) {
		runMainAndExit(new RealSystem(), args);
	}

	/**
	 * Runs main and exits
	 * @param system
	 * @args args from main()
	 */
	private static void runMainAndExit(JUnitSystem system, String... args) {
		Result result= new JUnitCore().runMain(system, args);
		System.exit(result.wasSuccessful() ? 0 : 1);
	}

	/**
	 * Run the tests contained in <code>classes</code>. Write feedback while the tests
	 * are running and write stack traces for all failed tests after all tests complete. This is
	 * similar to {@link #main(String[])}, but intended to be used programmatically.
	 * @param computer Helps construct Runners from classes
	 * @param classes Classes in which to find tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public static Result runClasses(Computer computer, Class<?>... classes) {
		return new JUnitCore().run(computer, classes);
	}
	/**
	 * Run the tests contained in <code>classes</code>. Write feedback while the tests
	 * are running and write stack traces for all failed tests after all tests complete. This is
	 * similar to {@link #main(String[])}, but intended to be used programmatically.
	 * @param classes Classes in which to find tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public static Result runClasses(Class<?>... classes) {
		return new JUnitCore().run(defaultComputer(), classes);
	}
	
	/**
	 * @param system 
	 * @args args from main()
	 */
	private Result runMain(JUnitSystem system, String... args) {
		system.out().println("JUnit version " + Version.id());
		List<Description> methods= new ArrayList<Description>();
		List<Class<?>> classes= new ArrayList<Class<?>>();
		List<Failure> missingClasses= new ArrayList<Failure>();
		for (String each : args)
			try {
				String[] class_method= each.split(SEPARATOR);
				if (class_method.length==2) { //found: ClassName#MethodName
					Class<?> clazz= Class.forName(class_method[0]);
					String methodName= class_method[1];
					final List<Description> foundMatchingMethods= findMatchingMethods(clazz, methodName);
					if (foundMatchingMethods.size()==0) {
						system.out().println("No matching method found for: " + methodName);
						Description description= Description.createSuiteDescription(each);
						Failure failure= new Failure(description, new NoTestsRemainException());
						missingClasses.add(failure);
					} else {
						methods.addAll(foundMatchingMethods);
						classes.add(clazz);
					}
				} else if (class_method.length==1 && each.endsWith(SEPARATOR)) { //found: ClassName#
					Class<?> clazz = Class.forName(class_method[0]);
					classes.add(clazz);
				} else { //assume ClassName otherwise, wrong format may cause ClassNotFoundException
					classes.add(Class.forName(each));
				}
			} catch (ClassNotFoundException e) {
				system.out().println("Could not find class: " + each);
				Description description= Description.createSuiteDescription(each);
				Failure failure= new Failure(description, e);
				missingClasses.add(failure);
			}
		RunListener listener= new TextListener(system);
		addListener(listener);
		Result result= run(createMethodFilter(methods), classes.toArray(new Class[0]));
		for (Failure each : missingClasses)
			result.getFailures().add(each);
		return result;
	}

	/**
	 * @return the version number of this release
	 */
	public String getVersion() {
		return Version.id();
	}

	/**
	 * Convert method name with wildcard into a list of {@link Description}
	 * @param clazz to search for methods matching <code>methodPattern</code>
	 * @param methodPattern  method name pattern
	 * @return list of matching descriptions
	 */
	private List<Description> findMatchingMethods(Class<?> clazz, String methodPattern) {
		List<Description> result = new ArrayList<Description>();
		for (Method m : clazz.getMethods()) {
			final String methodName= m.getName();
			if (methodName.matches(methodPattern)) {
				result.add(Description.createTestDescription(clazz, methodName));
			}
		}
		return result;
	}
	
	/**
	 * Construct new {@link Filter} based on method list.
	 * @param methods contains methods allowed by this filter
	 * @return new {@link Filter} to run only methods listed in <code>methods</code>
	 */
	private Filter createMethodFilter(final List<Description> methods) {
		final List<String> classNames= new ArrayList<String>();
		for (Description d : methods) {
			classNames.add(d.getClassName());
		}
		return new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				String methodName = description.getMethodName();
				String className = description.getClassName();
				if (methodName == null) {
					return true;
				}
				return classNames.contains(className)?methods.contains(description):true;
			}
			@Override
			public String describe() {
				return "command line method filter";
			}
		};
	}
	
	/**
	 * Run only test matching filter.
	 * @param filter applied to <code>classes</code> before run
	 * @param classes the classes containing tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 * @throws NoTestsRemainException
	 */
	public Result run(Filter filter, Class<?>... classes) {
		Request request = Request.classes(defaultComputer(), classes);
		if (filter!=null && classes.length>0) {
			request= request.filterWith(filter);
		}
		return run(request);
	}
	
	/**
	 * Run all the tests in <code>classes</code>.
	 * @param classes the classes containing tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public Result run(Class<?>... classes) {
		return run(Request.classes(defaultComputer(), classes));
	}

	/**
	 * Run all the tests in <code>classes</code>.
	 * @param computer Helps construct Runners from classes
	 * @param classes the classes containing tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public Result run(Computer computer, Class<?>... classes) {
		return run(Request.classes(computer, classes));
	}

	/**
	 * Run all the tests contained in <code>request</code>.
	 * @param request the request describing tests
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public Result run(Request request) {
		return run(request.getRunner());
	}

	/**
	 * Run all the tests contained in JUnit 3.8.x <code>test</code>. Here for backward compatibility.
	 * @param test the old-style test
	 * @return a {@link Result} describing the details of the test run and the failed tests.
	 */
	public Result run(junit.framework.Test test) { 
		return run(new JUnit38ClassRunner(test));
	}
	
	/**
	 * Do not use. Testing purposes only.
	 */
	public Result run(Runner runner) {
		Result result= new Result();
		RunListener listener= result.createListener();
		fNotifier.addFirstListener(listener);
		try {
			fNotifier.fireTestRunStarted(runner.getDescription());
			runner.run(fNotifier);
			fNotifier.fireTestRunFinished(result);
		} finally {
			removeListener(listener);
		}
		return result;
	}
	
	/**
	 * Add a listener to be notified as the tests run.
	 * @param listener the listener to add
	 * @see org.junit.runner.notification.RunListener
	 */
	public void addListener(RunListener listener) {
		fNotifier.addListener(listener);
	}

	/**
	 * Remove a listener.
	 * @param listener the listener to remove
	 */
	public void removeListener(RunListener listener) {
		fNotifier.removeListener(listener);
	}
	
	static Computer defaultComputer() {
		return new Computer();
	}

}
