package org.junit.internal.matchers;

import org.hamcrest.Matcher;
import org.hamcrest.core.Every;


/**
 * @deprecated use org.hamcrest.core.Every
 */
@Deprecated
public class Each {
	public static <T> Matcher<Iterable<T>> each(final Matcher<T> individual) {
		return Every.everyItem(individual);
	}
}
