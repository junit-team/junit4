package org.junit.internal.matchers;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.hasItem;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class Each {
	public static <T> Matcher<Iterable<? super T>> each(final Matcher<? super T> individual) {
		final Matcher<Iterable<? super T>> allItemsAre = not(hasItem(not(individual)));
		
		return new BaseMatcher<Iterable<? super T>>() {
			public boolean matches(Object item) {
				return allItemsAre.matches(item);
			}
			
			public void describeTo(Description description) {
				description.appendText("each ");
				individual.describeTo(description);
			}
		};
	}
}
