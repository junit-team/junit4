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
 * @see org.junit.Assume
 *
 * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
 */
@Deprecated
public class AssumptionViolatedException extends RuntimeException implements SelfDescribing {
    private static final long serialVersionUID = 2L;

    private final String assumption;

    private final boolean valueMatcher;
    private final Object value;

    private final Matcher<?> matcher;

    public AssumptionViolatedException(String assumption, boolean valueMatcher, Object value, Matcher<?> matcher) {
        super(value instanceof Throwable ? (Throwable) value : null);
        this.assumption = assumption;
        this.value = value;
        this.matcher = matcher;
        this.valueMatcher = valueMatcher;
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
    public AssumptionViolatedException(String assumption, Throwable e) {
        this(assumption, false, e, null);
    }

    @Override
    public String getMessage() {
        return StringDescription.asString(this);
    }

    public void describeTo(Description description) {
        if (assumption != null) {
            description.appendText(assumption);
        }

        if (valueMatcher) {
            if (assumption != null) {
                description.appendText(": ");
            }

            description.appendText("got: ");
            description.appendValue(value);

            if (matcher != null) {
                description.appendText(", expected: ");
                description.appendDescriptionOf(matcher);
            }
        }
    }
}