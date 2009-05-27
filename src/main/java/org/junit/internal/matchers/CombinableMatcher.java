package org.junit.internal.matchers;

import org.hamcrest.Matcher;

/**
 * @deprectated Use org.hamcrest.core.CombinableMatcher directly
 */
@Deprecated
public class CombinableMatcher<T> extends org.hamcrest.core.CombinableMatcher<T> {
	// should only be using static factories
	private CombinableMatcher(Matcher<? super T> matcher) {
		super(matcher);
	}
}