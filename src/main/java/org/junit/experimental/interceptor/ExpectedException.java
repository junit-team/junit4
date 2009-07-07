package org.junit.experimental.interceptor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.matches;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * The ExpectedException Rule allows in-test specification of expected exception
 * types and messages:
 * 
 * <pre>
 * // These tests all pass.
 * public static class HasExpectedException {
 * 	&#064;Rule
 * 	public ExpectedException thrown= new ExpectedException();
 * 
 * 	&#064;Test
 * 	public void throwsNothing() {
 *    // no exception expected, none thrown: passes.
 * 	}
 * 
 * 	&#064;Test
 * 	public void throwsNullPointerException() {
 * 		thrown.expect(NullPointerException.class);
 * 		throw new NullPointerException();
 * 	}
 * 
 * 	&#064;Test
 * 	public void throwsNullPointerExceptionWithMessage() {
 * 		thrown.expect(NullPointerException.class);
 * 		thrown.expectMessage(&quot;happened?&quot;);
 * 		thrown.expectMessage(startsWith(&quot;What&quot;));
 * 		throw new NullPointerException(&quot;What happened?&quot;);
 * 	}
 * }
 * </pre>
 */
public class ExpectedException implements MethodRule {
	public static ExpectedException none() {
		return new ExpectedException();
	}

	private Matcher<?> fMatcher= null;

	private ExpectedException() {
		
	}
	
	public Statement apply(Statement base, FrameworkMethod method, Object target) {
		return new ExpectedExceptionStatement(base);
	}

	/**
	 * Adds {@code matcher} to the list of requirements for any thrown exception.
	 */
	public void expect(Matcher<?> matcher) {
		if (fMatcher == null)
			fMatcher= matcher;
		else
			fMatcher= both(fMatcher).and(matches(matcher));
	}

	/**
	 * Adds to the list of requirements for any thrown exception that it
	 * should be an instance of {@code type}
	 */
	public void expect(Class<? extends Throwable> type) {
		expect(instanceOf(type));
	}

	/**
	 * Adds to the list of requirements for any thrown exception that it
	 * should <em>contain</em> string {@code substring}
	 */
	public void expectMessage(String substring) {
		expectMessage(containsString(substring));
	}

	/**
	 * Adds {@code matcher} to the list of requirements for the message 
	 * returned from any thrown exception.
	 */
	public void expectMessage(Matcher<String> matcher) {
		expect(hasMessage(matcher));
	}

	private class ExpectedExceptionStatement extends Statement {
		private final Statement fNext;

		public ExpectedExceptionStatement(Statement base) {
			fNext= base;
		}

		@Override
		public void evaluate() throws Throwable {
			try {
				fNext.evaluate();
			} catch (Throwable e) {
				if (fMatcher == null)
					throw e;
				Assert.assertThat(e, matches(fMatcher));
				return;
			}
			if (fMatcher != null)
				throw new AssertionError("Expected test to throw "
						+ StringDescription.toString(fMatcher));
		}
	}

	private Matcher<Throwable> hasMessage(Matcher<String> matcher) {
		return new FeatureMatcher<Throwable, String>(matcher,
				"exception with message", "getMessage()") {
			@Override
			protected String featureValueOf(Throwable actual) {
				return actual.getMessage();
			}
		};
	}
}
