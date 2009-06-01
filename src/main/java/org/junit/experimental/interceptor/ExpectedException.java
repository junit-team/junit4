package org.junit.experimental.interceptor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ExpectedException implements StatementInterceptor {
	private Matcher<Throwable> fMatcher= null;

	public Statement intercept(Statement base, FrameworkMethod method) {
		return new ExpectedExceptionStatement(base);
	}

	// TODO (Jun 1, 2009 3:56:50 PM): expect multiple things
	// TODO (Jun 1, 2009 4:26:59 PM): expect on original throwable
	public void expect(Class<? extends Throwable> type) {
		fMatcher= instanceOf(type);
	}

	public void expectMessage(String substring) {
		expectMessage(containsString(substring));
	}

	public void expectMessage(Matcher<String> matcher) {
		fMatcher= hasMessage(matcher);
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
				Assert.assertThat(e, fMatcher);
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
