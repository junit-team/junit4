package org.junit.internal.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * A matcher that applies a delegate matcher to the cause of the current Throwable, returning the result of that
 * match.
 *
 * @param <T> the type of the throwable being matched
 */
public class ThrowableCauseMatcher<T extends Throwable> extends
        TypeSafeMatcher<T> {

    private final Matcher<?> causeMatcher;

    public ThrowableCauseMatcher(Matcher<?> causeMatcher) {
        this.causeMatcher = causeMatcher;
    }

    public void describeTo(Description description) {
        description.appendText("exception with cause ");
        description.appendDescriptionOf(causeMatcher);
    }

    @Override
    protected boolean matchesSafely(T item) {
        return causeMatcher.matches(item.getCause());
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        description.appendText("cause ");
        causeMatcher.describeMismatch(item.getCause(), description);
    }

    /**
     * Returns a matcher that verifies that the outer exception has a cause for which the supplied matcher
     * evaluates to true.
     *
     * @param matcher to apply to the cause of the outer exception
     * @param <T> type of the outer exception
     */
    @Factory
    public static <T extends Throwable> Matcher<T> hasCause(final Matcher<?> matcher) {
        return new ThrowableCauseMatcher<T>(matcher);
    }
}