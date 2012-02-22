/*  Copyright (c) 2000-2006 hamcrest.org
 */
package org.hamcrest.core;

import static org.hamcrest.core.IsEqual.equalTo;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import org.hamcrest.BaseMatcher;


/**
 * Calculates the logical negation of a matcher.
 */
public class IsNot<T> extends BaseMatcher<T> {
    private final Matcher<T> matcher;

    public IsNot(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    public boolean matches(Object arg) {
        return !matcher.matches(arg);
    }

    public void describeTo(Description description) {
        description.appendText("not ").appendDescriptionOf(matcher);
    }

    /**
     * Inverts the rule.
     */
    @Factory
    public static <T> Matcher<T> not(Matcher<T> matcher) {
        return new IsNot<T>(matcher);
    }

    /**
     * This is a shortcut to the frequently used not(equalTo(x)).
     *
     * eg. assertThat(cheese, is(not(equalTo(smelly))))
     * vs  assertThat(cheese, is(not(smelly)))
     */
    @Factory
    public static <T> Matcher<T> not(T value) {
        return not(equalTo(value));
    }

}
