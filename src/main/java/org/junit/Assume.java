package org.junit;

/**
 * A set of methods useful for stating assumptions about the conditions in which a test is meaningful.
 * A failed assumption does not mean the code is broken, but that the test provides no useful information. Assume
 * basically means "don't run this test if these conditions don't apply". The default JUnit runner skips tests with
 * failing assumptions. Custom runners may behave differently.
 * <p>
 *     A good example of using assumptions is in <a href="https://github.com/junit-team/junit/wiki/Theories">Theories</a> where they are needed to exclude certain datapoints that aren't suitable or allowed for a certain test case.
 * </p>
 * Failed assumptions are usually not logged, because there may be many tests that don't apply to certain
 * configurations.
 *
 * <p>
 * These methods can be used directly: <code>Assume.assumeTrue(...)</code>, however, they
 * read better if they are referenced through static import:<br/>
 * <pre>
 * import static org.junit.Assume.*;
 *    ...
 *    assumeTrue(...);
 * </pre>
 * </p>
 *
 * @see <a href="https://github.com/junit-team/junit/wiki/Theories">Theories</a>
 *
 * @since 4.4
 */
public class Assume {
    /**
     * If called with an expression evaluating to {@code false}, the test will halt and be ignored.
     */
    public static void assumeTrue(boolean b) {
        assumeTrue(null, b);
    }

    /**
     * The inverse of {@link #assumeTrue(boolean)}.
     */
    public static void assumeFalse(boolean b) {
        assumeTrue(!b);
    }

    /**
     * If called with an expression evaluating to {@code false}, the test will halt and be ignored.
     *
     * @param b If <code>false</code>, the method will attempt to stop the test and ignore it by
     * throwing {@link AssumptionViolatedException}.
     * @param message A message to pass to {@link AssumptionViolatedException}.
     */
    public static void assumeTrue(String message, boolean b) {
        if (!b) throw new AssumptionViolatedException(message);
    }

    /**
     * The inverse of {@link #assumeTrue(String, boolean)}.
     */
    public static void assumeFalse(String message, boolean b) {
        assumeTrue(message, !b);
    }

    /**
     * If called with one or more null elements in <code>objects</code>, the test will halt and be ignored.
     */
    public static void assumeNotNull(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            assumeNotNull("objects[" + i + "] is not null", objects[i]);
        }
    }

    /**
     * If called with null, the test will halt and be ignored.
     */
    public static void assumeNotNull(Object object) {
        assumeNotNull("object is not null", object);
    }

    /**
     * If called with null, the test will halt and be ignored.
     */
    public static void assumeNotNull(String message, Object object) {
        assumeFalse(message, object == null);
    }

    /**
     * If called with null, the test will halt and be ignored.
     */
    public static void assumeNull(Object object) {
        assumeNull("object is null", object);
    }

    /**
     * If called with null, the test will halt and be ignored.
     */
    public static void assumeNull(String message, Object object) {
        assumeTrue(message, object == null);
    }

    /**
     * Use to assume that an operation completes normally.  If {@code e} is non-null, the test will halt and be ignored.
     *
     * For example:
     * <pre>
     * \@Test public void parseDataFile() {
     *   DataFile file;
     *   try {
     *     file = DataFile.open("sampledata.txt");
     *   } catch (IOException e) {
     *     // stop test and ignore if data can't be opened
     *     assumeNoException(e);
     *   }
     *   // ...
     * }
     * </pre>
     *
     * @param e if non-null, the offending exception
     */
    public static void assumeNoException(Throwable e) {
        assumeNoException("no exception", e);
    }

    /**
     * Attempts to halt the test and ignore it if Throwable <code>e</code> is
     * not <code>null</code>. Similar to {@link #assumeNoException(Throwable)},
     * but provides an additional message that can explain the details
     * concerning the assumption.
     *
     * @param e if non-null, the offending exception
     * @param message Additional message to pass to {@link AssumptionViolatedException}.
     * @see #assumeNoException(Throwable)
     */
    public static void assumeNoException(String message, Throwable e) {
        if (e != null) throw new AssumptionViolatedException(message, e);
    }
}
