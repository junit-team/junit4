package org.junit.internal.matchers;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * A matcher that delegates to throwableMatcher and in addition appends the
 * stacktrace of the actual Throwable in case of a mismatch.
 *
 * @deprecated use {@code org.hamcrest.junit.JunitMatchers.isThrowable()}
 * or {@code org.hamcrest.junit.JunitMatchers.isException()}
 */
@Deprecated
public class StacktracePrintingMatcher<T extends Throwable> extends
        org.hamcrest.TypeSafeMatcher<T> {

    private final Matcher<T> throwableMatcher;

    public StacktracePrintingMatcher(Matcher<T> throwableMatcher) {
        this.throwableMatcher = throwableMatcher;
    }

    public void describeTo(Description description) {
        throwableMatcher.describeTo(description);
    }

    @Override
    protected boolean matchesSafely(T item) {
        return throwableMatcher.matches(item);
    }

    @Override
    protected void describeMismatchSafely(T item, Description description) {
        throwableMatcher.describeMismatch(item, description);
        description.appendText("\nStacktrace was: ");
        description.appendText(readStacktrace(item));
    }

    private String readStacktrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @Factory
    public static <T extends Throwable> Matcher<T> isThrowable(
            Matcher<T> throwableMatcher) {
        return new StacktracePrintingMatcher<T>(throwableMatcher);
    }

    @Factory
    public static <T extends Exception> Matcher<T> isException(
            Matcher<T> exceptionMatcher) {
        return new StacktracePrintingMatcher<T>(exceptionMatcher);
    }
}
