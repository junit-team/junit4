package org.junit.internal;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

public class AssumptionViolatedException extends RuntimeException implements SelfDescribing {
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