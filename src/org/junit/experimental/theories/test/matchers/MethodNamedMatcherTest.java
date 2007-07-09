package org.junit.experimental.theories.test.matchers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.experimental.theories.matchers.api.MethodNamedMatcher;
import org.junit.experimental.theories.matchers.api.StackTrace;

public class MethodNamedMatcherTest {
	@Test public void noFactoryNameIfNoConstructor() {
		assertThat(StackTrace.create().factoryMethodName(), nullValue());
	}

	@Test public void requirementDescriptionIsBasedOnMethodName() {
		assertThat(hasAFrooble().toString(), is("has a frooble"));
	}

	private Matcher<Object> hasAFrooble() {
		return new MethodNamedMatcher<Object>() {
			public boolean matches(Object value) {
				return false;
			}
		};
	}

	@Test public void requirementDescriptionIsBasedOnMethodNameTriangulation() {
		assertThat(hasAWidget().toString(), is("has a widget"));
	}

	private Matcher<Object> hasAWidget() {
		return new MethodNamedMatcher<Object>() {
			public boolean matches(Object value) {
				return false;
			}
		};
	}

	@Test public void requirementDescriptionIsBasedOnMethodNameMoreTriangulation() {
		assertThat(doesNotAtAllExist().toString(), is("does not at all exist"));
	}

	private Matcher<Object> doesNotAtAllExist() {
		return new MethodNamedMatcher<Object>() {
			public boolean matches(Object value) {
				return false;
			}
		};
	}
}
