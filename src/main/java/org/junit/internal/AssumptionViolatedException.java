package org.junit.internal;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;
import org.junit.Assume;

/**
 * An exception class used to implement <i>assumptions</i> (state in which a given test 
 * is meaningful and should or should not be executed). A test for which an assumption 
 * fails should not generate a test case failure.
 * 
 * @see Assume
 */
public class AssumptionViolatedException extends RuntimeException implements SelfDescribing {
	private static final long serialVersionUID= 1L;

	private final Object fValue;

	private final Matcher<?> fMatcher;

	/**
	 * An assumption exception with the given <i>value</i> (String or 
	 * Throwable) and an additional failing {@link Matcher}.
	 */
	public AssumptionViolatedException(Object value, Matcher<?> matcher) {
		super(value instanceof Throwable ? (Throwable) value : null);
		fValue= value;
		fMatcher= matcher;
	}
	
	/**
	 * An assumption exception with the given message only.
	 */
	public AssumptionViolatedException(String assumption) {
		this(assumption, (Throwable) null);
	}

	/**
	 * An assumption exception with the given message and a cause.
	 */
	public AssumptionViolatedException(String message, Throwable t) {
		super(message, t);
		this.fValue = message;
		this.fMatcher = null;
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