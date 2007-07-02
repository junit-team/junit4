/**
 * 
 */
package org.junit.experimental.imposterization;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jmock.api.Invocation;
import org.jmock.api.Invokable;
import org.junit.Assert;

public class ThrownMatcher {
	public static class IncorrectThrownException extends RuntimeException {
		public IncorrectThrownException(Matcher<?> matcher, Throwable thrown) {
			super("Expected: " + matcher.toString(), thrown);
		}

		private static final long serialVersionUID = 1L;
	}

	private final Matcher<?> matcher;

	private ThrownMatcher(final Matcher<?> matcher) {
		this.matcher = matcher;
	}

	@SuppressWarnings("unchecked") public <T> T when(final T target) {
		return (T) createWhenObject(target);
	}

	private <T> Object createWhenObject(final T target) {
		return new PopperImposterizer(new Invokable() {
			public Object invoke(Invocation invocation) throws Throwable {
				try {
					invocation.applyTo(target);
				} catch (Throwable thrown) {
					if (!matcher.matches(thrown)) {
						throw new IncorrectThrownException(matcher, thrown);
					}
					return null;
				}

				Assert.assertThat(null, matcher);
				return null;
			}
		}).imposterize(target.getClass());
	}

	public static ThrownMatcher assertReturnsNormally() {
		return assertThrownException(Matchers.is(((Object) null)));
	}

	public static ThrownMatcher assertThrownException(Matcher<?> theMatcher) {
		return new ThrownMatcher(theMatcher);
	}
}