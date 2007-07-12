package org.junit.matchers;

import org.hamcrest.Description;

public abstract class SubstringMatcher extends TypeSafeMatcher<String> {

    // TODO: Replace String with CharSequence to allow for easy interopability between
    //       String, StringBuffer, StringBuilder, CharBuffer, etc (joe).

    protected final String substring;

    protected SubstringMatcher(final String substring) {
        this.substring = substring;
    }

    @Override
	public boolean matchesSafely(String item) {
        return evalSubstringOf(item);
    }

    public void describeTo(Description description) {
        description.appendText("a string ")
                .appendText(relationship())
                .appendText(" ")
                .appendValue(substring);
    }

    protected abstract boolean evalSubstringOf(String string);

    protected abstract String relationship();
}