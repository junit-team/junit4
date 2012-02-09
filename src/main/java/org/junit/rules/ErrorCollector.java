/**
 * 
 */
package org.junit.rules;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.hamcrest.Matcher;
import org.junit.runners.model.MultipleFailureException;

/**
 * The ErrorCollector rule allows execution of a test to continue after the
 * first problem is found (for example, to collect _all_ the incorrect rows in a
 * table, and report them all at once):
 * 
 * <pre>
 * public static class UsesErrorCollectorTwice {
 * 	&#064;Rule
 * 	public ErrorCollector collector= new ErrorCollector();
 * 
 * 	&#064;Test
 * 	public void example() {
 * 		collector.addError(new Throwable(&quot;first thing went wrong&quot;));
 * 		collector.addError(new Throwable(&quot;second thing went wrong&quot;));
 * 		collector.checkThat(getResult(), not(containsString(&quot;ERROR!&quot;)));
 * 		// all lines will run, and then a combined failure logged at the end.
 * 	}
 * }
 * </pre>
 */
public class ErrorCollector extends Verifier {
	private List<Throwable> errors= new ArrayList<Throwable>();

	@Override
	protected void verify() throws Throwable {
		MultipleFailureException.assertEmpty(errors);
	}

	/**
	 * Adds a Throwable to the table.  Execution continues, but the test will fail at the end.
	 */
	public void addError(Throwable error) {
		errors.add(error);
	}

	/**
	 * Adds a failure to the table if {@code matcher} does not match {@code value}.  
	 * Execution continues, but the test will fail at the end if the match fails.
	 */
	public <T> void checkThat(final T value, final Matcher<T> matcher) {
		checkThat("", value, matcher);
	}

	/**
	 * Adds a failure with the given {@code reason}
	 * to the table if {@code matcher} does not match {@code value}.
	 * Execution continues, but the test will fail at the end if the match fails.
	 */
	public <T> void checkThat(final String reason, final T value, final Matcher<T> matcher) {
		checkSucceeds(new Callable<Object>() {
			public Object call() throws Exception {
				assertThat(reason, value, matcher);
				return value;
			}
		});
	}

	/**
	 * Adds to the table the exception, if any, thrown from {@code callable}.
	 * Execution continues, but the test will fail at the end if
	 * {@code callable} threw an exception.
	 */
	public Object checkSucceeds(Callable<Object> callable) {
		try {
			return callable.call();
		} catch (Throwable e) {
			addError(e);
			return null;
		}
	}
}