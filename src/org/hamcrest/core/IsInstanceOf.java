/*  Copyright (c) 2000-2006 hamcrest.org
 */
package org.hamcrest.core;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import org.hamcrest.BaseMatcher;


/**
 * Tests whether the value is an instance of a class.
 */
public class IsInstanceOf extends BaseMatcher<Object> {
    private final Class<?> theClass;

    /**
     * Creates a new instance of IsInstanceOf
     *
     * @param theClass The predicate evaluates to true for instances of this class
     *                 or one of its subclasses.
     */
    public IsInstanceOf(Class<?> theClass) {
        this.theClass = theClass;
    }

    public boolean matches(Object item) {
        return theClass.isInstance(item);
    }

    public void describeTo(Description description) {
        description.appendText("an instance of ")
                .appendText(theClass.getName());
    }

    /**
     * Is the value an instance of a particular type?
     */
    @Factory
    public static Matcher<Object> instanceOf(Class<?> type) {
        return new IsInstanceOf(type);
    }

}
