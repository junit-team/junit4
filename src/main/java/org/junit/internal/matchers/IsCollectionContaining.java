package org.junit.internal.matchers;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

// Copied (hopefully temporarily) from hamcrest-library
public class IsCollectionContaining<T> extends TypeSafeMatcher<Iterable<T>> {
    private final Matcher<? extends T> elementMatcher;

    public IsCollectionContaining(Matcher<? extends T> elementMatcher) {
        this.elementMatcher = elementMatcher;
    }

    @Override
	public boolean matchesSafely(Iterable<T> collection) {
        for (T item : collection) {
            if (elementMatcher.matches(item)){
                return true;
            }
        }
        return false;
    }

    public void describeTo(Description description) {
        description
        	.appendText("a collection containing ")
        	.appendDescriptionOf(elementMatcher);
    }

    @Factory
    public static <T> Matcher<Iterable<T>> hasItem(Matcher<? extends T> elementMatcher) {
        return new IsCollectionContaining<T>(elementMatcher);
    }

    @Factory
    public static <T> Matcher<Iterable<T>> hasItem(T element) {
        return hasItem(equalTo(element));
    }

    @Factory
    public static <T> Matcher<Iterable<T>> hasItems(Matcher<? extends T>... elementMatchers) {
        Collection<Matcher<? extends Iterable<T>>> all
                = new ArrayList<Matcher<? extends Iterable<T>>>(elementMatchers.length);
        for (Matcher<? extends T> elementMatcher : elementMatchers) {
            all.add(hasItem(elementMatcher));
        }
        return allOf(all);
    }

    @Factory
    public static <T> Matcher<Iterable<T>> hasItems(T... elements) {
        Collection<Matcher<? extends Iterable<T>>> all
                = new ArrayList<Matcher<? extends Iterable<T>>>(elements.length);
        for (T element : elements) {
            all.add(hasItem(element));
        }
        return allOf(all);
    }

}
