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
 */
public class AssumptionViolatedException extends RuntimeException implements SelfDescribing {
    private static final long serialVersionUID = 2L;

    /*
     * We have to use the f prefix until the next major release to ensure
     * serialization compatibility. 
     * See https://github.com/junit-team/junit/issues/976
     */
    private final String fAssumption;
    private final boolean fValueMatcher;
    private final Object fValue;
    private final Matcher<?> fMatcher;

    /**
     * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
     */
    @Deprecated
    public AssumptionViolatedException(String assumption, boolean hasValue, Object value, Matcher<?> matcher) {
        this.fAssumption = assumption;
        this.fValue = value;
        this.fMatcher = matcher;
        this.fValueMatcher = hasValue;

        if (value instanceof Throwable) {
          initCause((Throwable) value);
        }
    }

    /**
     * An assumption exception with the given <i>value</i> (String or
     * Throwable) and an additional failing {@link Matcher}.
     *
     * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
     */
    @Deprecated
    public AssumptionViolatedException(Object value, Matcher<?> matcher) {
        this(null, true, value, matcher);
    }

    /**
     * An assumption exception with the given <i>value</i> (String or
     * Throwable) and an additional failing {@link Matcher}.
     *
     * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
     */
    @Deprecated
    public AssumptionViolatedException(String assumption, Object value, Matcher<?> matcher) {
        this(assumption, true, value, matcher);
    }

    /**
     * An assumption exception with the given message only.
     *
     * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
     */
    @Deprecated
    public AssumptionViolatedException(String assumption) {
        this(assumption, false, null, null);
    }

    /**
     * An assumption exception with the given message and a cause.
     *
     * @deprecated Please use {@link org.junit.AssumptionViolatedException} instead.
     */
    @Deprecated
    public AssumptionViolatedException(String assumption, Throwable e) {
        this(assumption, false, null, null);
        initCause(e);
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
            // a value was passed in when this instance was constructed; print it
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
