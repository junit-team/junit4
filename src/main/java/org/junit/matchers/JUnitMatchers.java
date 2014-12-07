package org.junit.matchers;

import org.hamcrest.Matcher;
import org.junit.internal.matchers.StacktracePrintingMatcher;

/**
 * Convenience import class: these are useful matchers for use with the assertThat method, but they are
 * not currently included in the basic CoreMatchers class from hamcrest.
 *
 * @since 4.4
 */
public class JUnitMatchers {

    /**
     * @return A matcher that delegates to throwableMatcher and in addition
     *         appends the stacktrace of the actual Throwable in case of a mismatch.
     */
    public static <T extends Throwable> Matcher<T> isThrowable(Matcher<T> throwableMatcher) {
        return StacktracePrintingMatcher.isThrowable(throwableMatcher);
    }

    /**
     * @return A matcher that delegates to exceptionMatcher and in addition
     *         appends the stacktrace of the actual Exception in case of a mismatch.
     */
    public static <T extends Exception> Matcher<T> isException(Matcher<T> exceptionMatcher) {
        return StacktracePrintingMatcher.isException(exceptionMatcher);
    }
}
