package org.junit.internal.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ThrowableMessageMatcher<T extends Throwable> extends
        TypeSafeMatcher<T> {

    private final Matcher<String> fMatcher;

    public ThrowableMessageMatcher(Matcher<String> matcher) {
        fMatcher = matcher;
    }

    public void describeTo(Description description) {
        description.appendText("exception with message ");
        description.appendDescriptionOf(fMatcher);
    }

    @Override
    protected boolean matchesSafely(T item) {
        return fMatcher.matches(item.getMessage());
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        description.appendText("message ");
        fMatcher.describeMismatch(item.getMessage(), description);
    }

    @Factory
    public static <T extends Throwable> Matcher<T> hasMessage(final Matcher<String> matcher) {
        return new ThrowableMessageMatcher<T>(matcher);
    }
}