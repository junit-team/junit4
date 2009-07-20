/*  Copyright (c) 2000-2006 hamcrest.org
 */
package org.junit.internal.matchers;

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Tests if the argument is a string that contains a substring.
 */
public class StringContains extends SubstringMatcher {
    public StringContains(String substring) {
        super(substring);
    }

    @Override
	protected boolean evalSubstringOf(String s) {
        return s.indexOf(substring) >= 0;
    }

    @Override
	protected String relationship() {
        return "containing";
    }

    @Factory
    public static Matcher<String> containsString(String substring) {
        return new StringContains(substring);
    }

}