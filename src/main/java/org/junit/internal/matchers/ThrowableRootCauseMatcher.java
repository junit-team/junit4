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
        description.appendText("exception with root cause ");
        description.appendDescriptionOf(fMatcher);
    }
    
    private Throwable getRoot(Throwable throwable) {
        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    @Override
    protected boolean matchesSafely(T item) {
        return fMatcher.matches(getRoot(item));
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        description.appendText("root cause ");
        fMatcher.describeMismatch(getRoot(item), description);
    }

    @Factory
    public static <T extends Throwable> Matcher<T> hasRootCause(final Matcher<T> matcher) {
        return new ThrowableRootCauseMatcher<T>(matcher);
    }
}