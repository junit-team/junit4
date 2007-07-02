package org.junit.experimental.theories.matchers.api;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public abstract class ClassNamedMatcher<T> extends BaseMatcher<T> {
	public void describeTo(Description description) {
		description.appendText(new CamelCaseName(getClass().getSimpleName())
				.asNaturalLanguage());
	}
}
