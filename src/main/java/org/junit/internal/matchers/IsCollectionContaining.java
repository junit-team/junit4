package org.junit.internal.matchers;

import org.hamcrest.Matcher;

// Copied (hopefully temporarily) from hamcrest-library
/**
 * @deprecated use org.hamcrest.core.IsCollectionContaining directly
 */
@Deprecated 
public class IsCollectionContaining<T> extends org.hamcrest.core.IsCollectionContaining<T> {
	// Client code should just use static factories, so this should be OK
	private IsCollectionContaining(Matcher<? super T> elementMatcher) {
		super(elementMatcher);
		// TODO Auto-generated constructor stub
	}
}
