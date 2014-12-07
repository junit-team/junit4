package org.junit;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 * An exception class used to implement <i>assumptions</i> (state in which a
 * given test is meaningful and should or should not be executed). A test for
 * which an assumption fails should not generate a test case failure.
 *
 * @see org.junit.Assume
 * @since 4.12
 */
public class AssumptionViolatedException extends RuntimeException implements
        SelfDescribing {

    private static final long serialVersionUID = 2L;

    private final String message;

    private final boolean hasValue;

    private final Object actualValue;

    private final Matcher<?> matcher;

    /**
     * An assumption exception with the given <i>actualValue</i> and a
     * <i>matcher</i> describing the expectation that failed.
     */
    public <T> AssumptionViolatedException(T actualValue, Matcher<T> matcher) {
        this(null, true, actualValue, matcher);
    }

    /**
     * An assumption exception with a message with the given <i>actualValue</i>
     * and a <i>matcher</i> describing the expectation that failed.
     */
    public <T> AssumptionViolatedException(String message, T actualValue,
            Matcher<T> matcher) {
        this(message, true, actualValue, matcher);
    }

    /**
     * An assumption exception with the given <i>message</i> only.
     */
    public AssumptionViolatedException(String message) {
        this(message, false, null, null);
    }

    /**
     * An assumption exception with the given <i>message</i> and <i>cause</i>.
     */
    public AssumptionViolatedException(String message, Throwable cause) {
        this(message, false, null, null);
        initCause(cause);
    }

    private AssumptionViolatedException(String message, boolean hasValue,
            Object actualValue, Matcher<?> matcher) {
        this.message = message;
        this.hasValue = hasValue;
        this.actualValue = actualValue;
        this.matcher = matcher;

        if (actualValue instanceof Throwable) {
            initCause((Throwable) actualValue);
        }
    }

    @Override
    public String getMessage() {
        return StringDescription.asString(this);
    }

    public void describeTo(Description description) {
        if (message != null) {
            description.appendText(message);
        }

        if (hasValue) {
            if (message != null) {
                description.appendText(": ");
            }

            description.appendText("got: ");
            description.appendValue(actualValue);

            if (matcher != null) {
                description.appendText(", expected: ");
                description.appendDescriptionOf(matcher);
            }
        }
    }
}
