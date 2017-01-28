package junit.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.fixtures.ClassWrapper;
import org.junit.fixtures.FixtureManager;
import org.junit.fixtures.InstanceMethod;
import org.junit.fixtures.TearDown;
import org.junit.fixtures.TestFixture;
import org.junit.runners.model.MultipleFailureException;

/**
 * A test case defines the fixture to run multiple tests. To define a test case<br/>
 * <ol>
 *   <li>implement a subclass of <code>TestCase</code></li>
 *   <li>define instance variables that store the state of the fixture</li>
 *   <li>initialize the fixture state by overriding {@link #setUp()}</li>
 *   <li>clean-up after a test by overriding {@link #tearDown()}.</li>
 * </ol>
 * Each test runs in its own fixture so there
 * can be no side effects among test runs.
 * Here is an example:
 * <pre>
 * public class MathTest extends TestCase {
 *    protected double fValue1;
 *    protected double fValue2;
 *
 *    protected void setUp() {
 *       fValue1= 2.0;
 *       fValue2= 3.0;
 *    }
 * }
 * </pre>
 *
 * For each test implement a method which interacts
 * with the fixture. Verify the expected results with assertions specified
 * by calling {@link junit.framework.Assert#assertTrue(String, boolean)} with a boolean.
 * <pre>
 *    public void testAdd() {
 *       double result= fValue1 + fValue2;
 *       assertTrue(result == 5.0);
 *    }
 * </pre>
 *
 * Once the methods are defined you can run them. The framework supports
 * both a static type safe and more dynamic way to run a test.
 * In the static way you override the runTest method and define the method to
 * be invoked. A convenient way to do so is with an anonymous inner class.
 * <pre>
 * TestCase test= new MathTest("add") {
 *    public void runTest() {
 *       testAdd();
 *    }
 * };
 * test.run();
 * </pre>
 * The dynamic way uses reflection to implement {@link #runTest()}. It dynamically finds
 * and invokes a method.
 * In this case the name of the test case has to correspond to the test method
 * to be run.
 * <pre>
 * TestCase test= new MathTest("testAdd");
 * test.run();
 * </pre>
 *
 * The tests to be run can be collected into a TestSuite. JUnit provides
 * different <i>test runners</i> which can run a test suite and collect the results.
 * A test runner either expects a static method <code>suite</code> as the entry
 * point to get a test to run or it will extract the suite automatically.
 * <pre>
 * public static Test suite() {
 *    suite.addTest(new MathTest("testAdd"));
 *    suite.addTest(new MathTest("testDivideByZero"));
 *    return suite;
 * }
 * </pre>
 *
 * @see TestResult
 * @see TestSuite
 */
@SuppressWarnings("deprecation")
public abstract class TestCase extends Assert implements Test {
    /**
     * the name of the test case
     */
    private String fName;

    private FixtureManager fixtureManager;
    private boolean testStarted = false;
    private final List<TestFixture> testFixtures = new ArrayList<TestFixture>();
    private Method testMethod;

    /**
     * No-arg constructor to enable serialization. This method
     * is not intended to be used by mere mortals without calling setName().
     */
    public TestCase() {
        fName = null;
    }

    /**
     * Constructs a test case with the given name.
     */
    public TestCase(String name) {
        fName = name;
    }

    /**
     * Adds a test fixture to the current test. If {@link #setUp()} has been called
     * then the fixture will be initialized right away. Otherwise, the fixture will
     * be initialized before {@code setUp()} is called.
     *
     * @param testFixture
     * @throws Exception exception thrown during {@code TestFixture#initialize(FixtureContext)}
     */
    public final void addTestFixture(TestFixture testFixture) throws Throwable {
        if (testStarted) {
            getOrCreateFixtureManager().initializeFixture(testFixture);
        } else {
            testFixtures.add(testFixture);
        }
    }

    /**
     * Adds a {@link TearDown} to run after the test completes.
     * Cannot be called before {@link #setUp()} is called.
     */
    public final void addTearDown(TearDown tearDown) {
        if (!testStarted) {
             throw new IllegalStateException("Cannot call addTeadrDown() before setUp() is called");
        }
        getOrCreateFixtureManager().addTearDown(tearDown);
    }

    /**
     * Gets or creates the fixture manager.
     *
     * @throws AssertionFailedError if the test method doesn't exist
     */
    private FixtureManager getOrCreateFixtureManager() {
        if (fixtureManager == null) {
            Method testMethod = getTestMethod(); // Can throw AssertionFailedError
            fixtureManager = FixtureManager.forTestMethod(
                    new InstanceMethod(new ClassWrapper(getClass()), testMethod, this));
        }
        return fixtureManager;
    }

    /**
     * Counts the number of test cases executed by run(TestResult result).
     */
    public int countTestCases() {
        return 1;
    }

    /**
     * Creates a default TestResult object.
     *
     * @see TestResult
     */
    protected TestResult createResult() {
        return new TestResult();
    }

    /**
     * A convenience method to run this test, collecting the results with a
     * default TestResult object.
     *
     * @see TestResult
     */
    public TestResult run() {
        TestResult result = createResult();
        run(result);
        return result;
    }

    /**
     * Runs the test case and collects the results in TestResult.
     */
    public void run(TestResult result) {
        result.run(this);
    }

    /**
     * Runs the bare test sequence.
     *
     * @throws Throwable if any exception is thrown
     */
    public void runBare() throws Throwable {
        initializeTestFixtures();
        List<Throwable> tearDownErrors = new ArrayList<Throwable>();
        try {
            setUp();
        } catch (Throwable e) {
            runAllTearDowns(tearDownErrors);
            throw e;
        }

        Throwable exception = null;
        try {
            runTest();
            runAllPostconditions();
        } catch (Throwable running) {
            exception = running;
        } finally {
            try {
                tearDown();
            } catch (Throwable tearingDown) {
                if (exception == null) exception = tearingDown;
            }
            runAllTearDowns(tearDownErrors);
        }
        if (exception != null) throw exception;
        MultipleFailureException.assertEmpty(tearDownErrors);
    }

    private void initializeTestFixtures() throws Throwable {
        testStarted = true;

        if (fixtureManager == null) {
            if (testFixtures.isEmpty()) {
                return;
            }
            getOrCreateFixtureManager();
        }

        try {
            for (TestFixture testFixture : testFixtures) {
                fixtureManager.initializeFixture(testFixture);
            }
        } finally {
            testFixtures.clear();
        }
    }

    private void runAllPostconditions() throws Exception {
        if (fixtureManager == null) {
            return;
        }
        fixtureManager.runAllPostconditions();
    }

    private void runAllTearDowns(List<Throwable> tearDownErrors) {
        if (fixtureManager == null) {
            return;
        }
        fixtureManager.runAllTearDowns(tearDownErrors);
        fixtureManager.reset();
    }

    /**
     * Override to run the test and assert its state.
     *
     * @throws Throwable if any exception is thrown
     */
    protected void runTest() throws Throwable {
        Method runMethod = getTestMethod();

        try {
            runMethod.invoke(this);
        } catch (InvocationTargetException e) {
            e.fillInStackTrace();
            throw e.getTargetException();
        } catch (IllegalAccessException e) {
            e.fillInStackTrace();
            throw e;
        }
    }

    private Method getTestMethod() {
        if (testMethod != null) {
            return testMethod;
        }
        assertNotNull("TestCase.fName cannot be null", fName); // Some VMs crash when calling getMethod(null,null);
        try {
            // use getMethod to get all public inherited
            // methods. getDeclaredMethods returns all
            // methods of this class but excludes the
            // inherited ones.
            Method runMethod = getClass().getMethod(fName, (Class[]) null);
            if (!Modifier.isPublic(runMethod.getModifiers())) {
                fail("Method \"" + fName + "\" should be public");
            }
            testMethod = runMethod;
            return testMethod;
        } catch (NoSuchMethodException e) {
            fail("Method \"" + fName + "\" not found");
        }
        return null; // we never get here
    }

    /**
     * Asserts that a condition is true. If it isn't it throws
     * an AssertionFailedError with the given message.
     */
    public static void assertTrue(String message, boolean condition) {
        Assert.assertTrue(message, condition);
    }

    /**
     * Asserts that a condition is true. If it isn't it throws
     * an AssertionFailedError.
     */
    public static void assertTrue(boolean condition) {
        Assert.assertTrue(condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws
     * an AssertionFailedError with the given message.
     */
    public static void assertFalse(String message, boolean condition) {
        Assert.assertFalse(message, condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws
     * an AssertionFailedError.
     */
    public static void assertFalse(boolean condition) {
        Assert.assertFalse(condition);
    }

    /**
     * Fails a test with the given message.
     */
    public static void fail(String message) {
        Assert.fail(message);
    }

    /**
     * Fails a test with no message.
     */
    public static void fail() {
        Assert.fail();
    }

    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, Object expected, Object actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown.
     */
    public static void assertEquals(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that two Strings are equal.
     */
    public static void assertEquals(String message, String expected, String actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two Strings are equal.
     */
    public static void assertEquals(String expected, String actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that two doubles are equal concerning a delta.  If they are not
     * an AssertionFailedError is thrown with the given message.  If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(String message, double expected, double actual, double delta) {
        Assert.assertEquals(message, expected, actual, delta);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(double expected, double actual, double delta) {
        Assert.assertEquals(expected, actual, delta);
    }

    /**
     * Asserts that two floats are equal concerning a positive delta. If they
     * are not an AssertionFailedError is thrown with the given message. If the
     * expected value is infinity then the delta value is ignored.
     */
    public static void assertEquals(String message, float expected, float actual, float delta) {
        Assert.assertEquals(message, expected, actual, delta);
    }

    /**
     * Asserts that two floats are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(float expected, float actual, float delta) {
        Assert.assertEquals(expected, actual, delta);
    }

    /**
     * Asserts that two longs are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, long expected, long actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two longs are equal.
     */
    public static void assertEquals(long expected, long actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that two booleans are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, boolean expected, boolean actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two booleans are equal.
     */
    public static void assertEquals(boolean expected, boolean actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that two bytes are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, byte expected, byte actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two bytes are equal.
     */
    public static void assertEquals(byte expected, byte actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that two chars are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, char expected, char actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two chars are equal.
     */
    public static void assertEquals(char expected, char actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that two shorts are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, short expected, short actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two shorts are equal.
     */
    public static void assertEquals(short expected, short actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that two ints are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, int expected, int actual) {
        Assert.assertEquals(message, expected, actual);
    }

    /**
     * Asserts that two ints are equal.
     */
    public static void assertEquals(int expected, int actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Asserts that an object isn't null.
     */
    public static void assertNotNull(Object object) {
        Assert.assertNotNull(object);
    }

    /**
     * Asserts that an object isn't null. If it is
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertNotNull(String message, Object object) {
        Assert.assertNotNull(message, object);
    }

    /**
     * Asserts that an object is null. If it isn't an {@link AssertionError} is
     * thrown.
     * Message contains: Expected: <null> but was: object
     *
     * @param object Object to check or <code>null</code>
     */
    public static void assertNull(Object object) {
        Assert.assertNull(object);
    }

    /**
     * Asserts that an object is null.  If it is not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertNull(String message, Object object) {
        Assert.assertNull(message, object);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not
     * an AssertionFailedError is thrown with the given message.
     */
    public static void assertSame(String message, Object expected, Object actual) {
        Assert.assertSame(message, expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not
     * the same an AssertionFailedError is thrown.
     */
    public static void assertSame(Object expected, Object actual) {
        Assert.assertSame(expected, actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object an AssertionFailedError is thrown with the
     * given message.
     */
    public static void assertNotSame(String message, Object expected, Object actual) {
        Assert.assertNotSame(message, expected, actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object an AssertionFailedError is thrown.
     */
    public static void assertNotSame(Object expected, Object actual) {
        Assert.assertNotSame(expected, actual);
    }

    public static void failSame(String message) {
        Assert.failSame(message);
    }

    public static void failNotSame(String message, Object expected, Object actual) {
        Assert.failNotSame(message, expected, actual);
    }

    public static void failNotEquals(String message, Object expected, Object actual) {
        Assert.failNotEquals(message, expected, actual);
    }

    public static String format(String message, Object expected, Object actual) {
        return Assert.format(message, expected, actual);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    protected void setUp() throws Exception {
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    protected void tearDown() throws Exception {
    }

    /**
     * Returns a string representation of the test case.
     */
    @Override
    public String toString() {
        return getName() + "(" + getClass().getName() + ")";
    }

    /**
     * Gets the name of a TestCase.
     *
     * @return the name of the TestCase
     */
    public String getName() {
        return fName;
    }

    /**
     * Sets the name of a TestCase.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        fName = name;
    }
}
