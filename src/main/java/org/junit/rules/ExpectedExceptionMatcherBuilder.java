package org.junit.rules;

import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.matchers.JUnitMatchers.isThrowable;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

/**
 * Builds special matcher used by {@link ExpectedException}.
 */
class ExpectedExceptionMatcherBuilder {

    private final List<Matcher<?>> fMatchers = new ArrayList<Matcher<?>>();

    void add(Matcher<?> matcher) {
        fMatchers.add(matcher);
    }

    boolean expectsThrowable() {
        return !fMatchers.isEmpty();
    }

    Matcher<Throwable> build() {
        return isThrowable(allOfTheMatchers());
    }

    private Matcher<Throwable> allOfTheMatchers() {
        if (fMatchers.size() == 1) {
            return cast(fMatchers.get(0));
        }
        return allOf(castedMatchers());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<Matcher<? super Throwable>> castedMatchers() {
        return new ArrayList<Matcher<? super Throwable>>((List) fMatchers);
    }

    @SuppressWarnings("unchecked")
    private Matcher<Throwable> cast(Matcher<?> singleMatcher) {
        return (Matcher<Throwable>) singleMatcher;
    }
}
