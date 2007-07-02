package org.junit.experimental.theories.matchers.api;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public abstract class MethodNamedMatcher<T> extends BaseMatcher<T> {
	private StackTrace constructionStackTrace;

	public MethodNamedMatcher() {
		this.constructionStackTrace = StackTrace.create();
	}

	public void describeTo(Description description) {
		description.appendText(new CamelCaseName(constructionStackTrace
				.factoryMethodName()).asNaturalLanguage());
	}
}
