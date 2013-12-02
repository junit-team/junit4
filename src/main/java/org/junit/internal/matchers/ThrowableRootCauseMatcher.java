package org.junit.internal.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ThrowableRootCauseMatcher<T extends Throwable> extends
        TypeSafeMatcher<T> {

    private final Matcher<T> fMatcher;

    public ThrowableRootCauseMatcher(Matcher<T> matcher) {
        fMatcher = matcher;
    }

    public void describeTo(Description description) {
        description.appendText("exception with cause ");
        description.appendDescriptionOf(fMatcher);
    }

    @Override
    protected boolean matchesSafely(T item) {
        Throwable exception = item;
        while (exception.getCause() != null) {
            exception = exception.getCause();
        }
        return fMatcher.matches(exception);
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        description.appendText("cause ");
        fMatcher.describeMismatch(item.getCause(), description);
    }

    @Factory
    public static <T extends Throwable> Matcher<T> hasRootCause(final Matcher<T> matcher) {
        return new ThrowableRootCauseMatcher<T>(matcher);
    }
}