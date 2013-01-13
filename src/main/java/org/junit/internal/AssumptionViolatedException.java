package org.junit.internal;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 * An exception class used to implement <i>assumptions</i> (state in which a given test
 * is meaningful and should or should not be executed). A test for which an assumption
 * fails should not generate a test case failure.
 *
 * @see Assume
 */
public class AssumptionViolatedException extends RuntimeException implements SelfDescribing {
    private static final long serialVersionUID = 2L;

    private final String fAssumption;

    private final boolean fValueMatcher;
    private final Object fValue;

    private final Matcher<?> fMatcher;

    public AssumptionViolatedException(String assumption, boolean valueMatcher, Object value, Matcher<?> matcher) {
        super(value instanceof Throwable ? (Throwable) value : null);
        fAssumption = assumption;
        fValue = value;
        fMatcher = matcher;
        fValueMatcher = valueMatcher;
    }

    /**
     * An assumption exception with the given <i>value</i> (String or
     * Throwable) and an additional failing {@link Matcher}.
     */
    public AssumptionViolatedException(Object value, Matcher<?> matcher) {
        this(null, true, value, matcher);
    }

    /**
     * An assumption exception with the given <i>value</i> (String or
     * Throwable) and an additional failing {@link Matcher}.
     */
    public AssumptionViolatedException(String assumption, Object value, Matcher<?> matcher) {
        this(assumption, true, value, matcher);
    }

    /**
     * An assumption exception with the given message only.
     */
    public AssumptionViolatedException(String assumption) {
        this(assumption, false, null, null);
    }

    /**
     * An assumption exception with the given message and a cause.
     */
    public AssumptionViolatedException(String assumption, Throwable t) {
        this(assumption, false, t, null);
    }

    @Override
    public String getMessage() {
        return StringDescription.asString(this);
    }

    public void describeTo(Description description) {
        if (fAssumption != null) {
            description.appendText(fAssumption);
        }

        if (fValueMatcher) {
            if (fAssumption != null) {
                description.appendText(": ");
            }

            description.appendText("got: ");
            description.appendValue(fValue);

            if (fMatcher != null) {
                description.appendText(", expected: ");
                description.appendDescriptionOf(fMatcher);
            }
        }
    }
}