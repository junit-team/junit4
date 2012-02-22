package org.hamcrest.core;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsEqual.equalTo;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Decorates another Matcher, retaining the behavior but allowing tests
 * to be slightly more expressive.
 *
 * eg. assertThat(cheese, equalTo(smelly))
 * vs  assertThat(cheese, is(equalTo(smelly)))
 */
public class Is<T> extends BaseMatcher<T> {

    private final Matcher<T> matcher;

    public Is(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    public boolean matches(Object arg) {
        return matcher.matches(arg);
    }

    public void describeTo(Description description) {
        description.appendText("is ").appendDescriptionOf(matcher);
    }
    
    /**
     * Decorates another Matcher, retaining the behavior but allowing tests
     * to be slightly more expressive.
     *
     * eg. assertThat(cheese, equalTo(smelly))
     * vs  assertThat(cheese, is(equalTo(smelly)))
     */
    @Factory
    public static <T> Matcher<T> is(Matcher<T> matcher) {
        return new Is<T>(matcher);
    }

    /**
     * This is a shortcut to the frequently used is(equalTo(x)).
     *
     * eg. assertThat(cheese, is(equalTo(smelly)))
     * vs  assertThat(cheese, is(smelly))
     */
    @Factory
    public static <T> Matcher<T> is(T value) {
        return is(equalTo(value));
    }

    /**
     * This is a shortcut to the frequently used is(instanceOf(SomeClass.class)).
     *
     * eg. assertThat(cheese, is(instanceOf(Cheddar.class)))
     * vs  assertThat(cheese, is(Cheddar.class))
     */
    @Factory
    public static Matcher<Object> is(Class<?> type) {
        return is(instanceOf(type));
    }

}

