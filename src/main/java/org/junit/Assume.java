package org.junit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.hamcrest.Matcher;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.matchers.Each;

/**
 * A set of methods useful for stating assumptions about the conditions in which a test is meaningful.
 * A failed assumption does not mean the code is broken, but that the test provides no useful information.
 * The default JUnit runner treats tests with failing assumptions as ignored.  Custom runners may behave differently.
 * 
 * For example:
 * <pre>
 * // only provides information if database is reachable.
 * \@Test public void calculateTotalSalary() {
 *    DBConnection dbc = Database.connect();
 *    assumeNotNull(dbc);
 *    // ...
 * }
 * </pre>
 * These methods can be used directly: <code>Assume.assumeTrue(...)</code>, however, they
 * read better if they are referenced through static import:<br/>
 * <pre>
 * import static org.junit.Assume.*;
 *    ...
 *    assumeTrue(...);
 * </pre>
 */
public class Assume {
	/**
	 * If called with an expression evaluating to {@code false}, the test will halt and be ignored.
	 * @param b
	 */
	public static void assumeTrue(boolean b) {
		assumeThat(b, is(true));
	}

	/**
	 * If called with one or more null elements in <code>objects</code>, the test will halt and be ignored.
	 * @param objects
	 */
		public static void assumeNotNull(Object... objects) {
		assumeThat(asList(objects), Each.each(notNullValue()));
	}

	    /**
	     * Call to assume that <code>actual</code> satisfies the condition specified by <code>matcher</code>.
	     * If not, the test halts and is ignored.
	     * Example:
	     * <pre>:
	     *   assumeThat(1, is(1)); // passes
	     *   foo(); // will execute
	     *   assumeThat(0, is(1)); // assumption failure! test halts
	     *   int x = 1 / 0; // will never execute
	     * </pre>
	     *   
	     * @param <T> the static type accepted by the matcher (this can flag obvious compile-time problems such as {@code assumeThat(1, is("a"))}
	     * @param actual the computed value being compared
	     * @param matcher an expression, built of {@link Matcher}s, specifying allowed values
	     * 
	     * @see org.hamcrest.CoreMatchers
	     * @see org.junit.matchers.JUnitMatchers
	     */
	public static <T> void assumeThat(T actual, Matcher<T> matcher) {
		if (!matcher.matches(actual))
			throw new AssumptionViolatedException(actual, matcher); 
	}

    /**
	 * Use to assume that an operation completes normally.  If {@code t} is non-null, the test will halt and be ignored.
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
	 * @param t if non-null, the offending exception
	 */
	public static void assumeNoException(Throwable t) {
		assumeThat(t, nullValue());
	}
}
