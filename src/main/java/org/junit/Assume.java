package org.junit;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;
import org.junit.matchers.Each;

public class Assume {
	public static class AssumptionViolatedException extends RuntimeException implements SelfDescribing {
		private static final long serialVersionUID= 1L;

		private final Object fValue;

		private final Matcher<?> fMatcher;

		public AssumptionViolatedException(Object value, Matcher<?> matcher) {
			super(value instanceof Throwable ? (Throwable) value : null);
			fValue= value;
			fMatcher= matcher;
		}
		
		public AssumptionViolatedException(String assumption) {
			this(assumption, null);
		}

		@Override
		public String getMessage() {
			return StringDescription.asString(this);
		}

		public void describeTo(Description description) {
			if (fMatcher != null) {
				description.appendText("got: ");
				description.appendValue(fValue);
				description.appendText(", expected: ");
				description.appendDescriptionOf(fMatcher);
			} else {
				description.appendText("failed assumption: " + fValue);
			}
		}
	}

	public static <T> void assumeThat(T value, Matcher<T> assumption) {
		if (!assumption.matches(value))
			throw new AssumptionViolatedException(value, assumption);
	}

	public static void assumeNotNull(Object... objects) {
		assumeThat(asList(objects), Each.each(notNullValue()));
	}

	public static void assumeNoException(Throwable t) {
		assumeThat(t, nullValue());
	}

	public static void assumeTrue(boolean b) {
		assumeThat(b, is(true));
	}

	public static void fail(String string) {
		// TODO: (Dec 7, 2007 11:16:24 AM) something that looks better

		assumeThat(string, nullValue());
	}
}
