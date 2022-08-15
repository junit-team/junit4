package org.junit.matchers;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.core.CombinableMatcher.CombinableBothMatcher;
import org.hamcrest.core.CombinableMatcher.CombinableEitherMatcher;
import org.junit.internal.matchers.StacktracePrintingMatcher;

/**
 * Convenience import class: these are useful matchers for use with the assertThat method, but they are
 * not currently included in the basic CoreMatchers class from hamcrest.
 *
 * @since 4.4
 */
public class JUnitMatchers {
    /**
     * @return A matcher matching any collection containing element
     * @deprecated Please use {@link CoreMatchers#hasItem(Object)} instead.
     */
    @Deprecated
    public static <T> Matcher<Iterable<? super T>> hasItem(T element) {
        return CoreMatchers.hasItem(element);
    }

    /**
     * @return A matcher matching any collection containing an element matching elementMatcher
     * @deprecated Please use {@link CoreMatchers#hasItem(Matcher)} instead.
     */
    @Deprecated
    public static <T> Matcher<Iterable<? super T>> hasItem(Matcher<? super T> elementMatcher) {
        return CoreMatchers.<T>hasItem(elementMatcher);
    }

    /**
     * @return A matcher matching any collection containing every element in elements
     * @deprecated Please use {@link CoreMatchers#hasItems(Object...)} instead.
     */
    @Deprecated
    public static <T> Matcher<Iterable<T>> hasItems(T... elements) {
        return CoreMatchers.hasItems(elements);
    }

    /**
     * @return A matcher matching any collection containing at least one element that matches
     *         each matcher in elementMatcher (this may be one element matching all matchers,
     *         or different elements matching each matcher)
     * @deprecated Please use {@link CoreMatchers#hasItems(Matcher...)} instead.
     */
    @Deprecated
    public static <T> Matcher<Iterable<T>> hasItems(Matcher<? super T>... elementMatchers) {
        return CoreMatchers.hasItems(elementMatchers);
    }

    /**
     * @return A matcher matching any collection in which every element matches elementMatcher
     * @deprecated Please use {@link CoreMatchers#everyItem(Matcher)} instead.
     */
    @Deprecated
    public static <T> Matcher<Iterable<T>> everyItem(final Matcher<T> elementMatcher) {
        return CoreMatchers.everyItem(elementMatcher);
    }

    /**
     * @return a matcher matching any string that contains substring
     * @deprecated Please use {@link CoreMatchers#containsString(String)} instead.
     */
    @Deprecated
    public static Matcher<java.lang.String> containsString(java.lang.String substring) {
        return CoreMatchers.containsString(substring);
    }

    /**
     * This is useful for fluently combining matchers that must both pass.  For example:
     * <pre>
     *   assertThat(string, both(containsString("a")).and(containsString("b")));
     * </pre>
     *
     * @deprecated Please use {@link CoreMatchers#both(Matcher)} instead.
     */
    @Deprecated
    public static <T> CombinableBothMatcher<T> both(Matcher<? super T> matcher) {
        return CoreMatchers.both(matcher);
    }

    /**
     * This is useful for fluently combining matchers where either may pass, for example:
     * <pre>
     *   assertThat(string, either(containsString("a")).or(containsString("b")));
     * </pre>
     *
     * @deprecated Please use {@link CoreMatchers#either(Matcher)} instead.
     */
    @Deprecated
    public static <T> CombinableEitherMatcher<T> either(Matcher<? super T> matcher) {
        return CoreMatchers.either(matcher);
    }

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
