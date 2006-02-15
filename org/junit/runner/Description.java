package org.junit.runner;

import java.util.ArrayList;

/**
 * A <code>Description</code> describes a test which is to be run or has been run. <code>Descriptions</code> can
 * be atomic (a single test) or compound (containing children tests). <code>Descriptions</code> are used
 * to provide feedback about the tests that are about to run (for example, the tree view
 * visible in many IDEs) or tests that have been run (for example, the failures view). <p>
 * <code>Descriptions</code> are implemented as a single class rather than a Composite because
 * they are entirely informational. They contain no logic aside from counting their tests. <p>
 * In the past, we used the raw <code>junit.framework.TestCase</code>s and <code>junit.framework.TestSuite</code>s
 * to display the tree of tests. This was no longer viable in JUnit 4 because atomic tests no longer have a superclass below <code>Object</code>.
 * We needed a way to pass a class and name together. Description emerged from this.
 * 
 * @see org.junit.runner.Request
 * @see org.junit.runner.Runner
 */
public class Description {
	
	/**
	 * Create a <code>Description</code> named <code>name</code>.
	 * Generally, you will add children to this <code>Description</code>.
	 * @param name The name of the <code>Description</code> 
	 * @return A <code>Description</code> named <code>name</code>
	 */
	public static Description createSuiteDescription(String name) {
		return new Description(name);
	}

	/**
	 * Create a <code>Description</code> of a single test named <code>name</code> in the class <code>clazz</code>.
	 * Generally, this will be a leaf <code>Description</code>.
	 * @param clazz The class of the test
	 * @param name The name of the test (a method name for test annotated with <code>@Test</code>)
	 * @return A <code>Description</code> named <code>name</code>
	 */
	public static Description createTestDescription(Class clazz, String name) {
		return new Description(String.format("%s(%s)", name, clazz.getName()));
	}
	
	/**
	 * Create a generic <code>Description</code> that says there are tests in <code>testClass</code>.
	 * This is used as a last resort when you cannot precisely describe the individual tests in the class.
	 * @param testClass A <code>Class</code> containing tests 
	 * @return A <code>Description</code> of <code>testClass</code>
	 */
	public static Description createSuiteDescription(Class<?> testClass) {
		return new Description(testClass.getName());
	}
	
	public static Description TEST_MECHANISM = new Description("Test mechanism");
	private final ArrayList<Description> fChildren= new ArrayList<Description>();
	private final String fDisplayName;

	//TODO we seem to be using the static factories exclusively
	private Description(final String displayName) {
		fDisplayName= displayName;
	}

	/**
	 * @return a user-understandable label
	 */
	public String getDisplayName() {
		return fDisplayName;
	}

	/**
	 * Add <code>description</code> as a child of the receiver.
	 * @param description The soon-to-be child.
	 */
	public void addChild(Description description) {
		getChildren().add(description);
	}

	/**
	 * @return the receiver's children, if any
	 */
	public ArrayList<Description> getChildren() {
		return fChildren;
	}

	/**
	 * @return true if the receiver is a suite
	 */
	public boolean isSuite() {
		return !isTest();
	}

	/**
	 * @return true if the receiver is an atomic test
	 */
	public boolean isTest() {
		return getChildren().isEmpty();
	}

	/**
	 * @return the total number of atomic tests in the receiver
	 */
	public int testCount() {
		if (isTest())
			return 1;
		int result= 0;
		for (Description child : getChildren())
			result+= child.testCount();
		return result;
	}

	@Override
	public int hashCode() {
		return getDisplayName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Description))
			return false;
		Description d = (Description) obj;
		return getDisplayName().equals(d.getDisplayName())
				&& getChildren().equals(d.getChildren());
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}
}
