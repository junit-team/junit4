/**
 * 
 */
package org.junit.experimental.interceptor;

import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.hamcrest.Matcher;
import org.junit.internal.runners.model.MultipleFailureException;

public class ErrorCollector extends Verifier {
	private List<Throwable> errors= new ArrayList<Throwable>();

	@Override
	protected void verify() throws Throwable {
		MultipleFailureException.assertEmpty(errors);
	}

	public void addError(Throwable error) {
		errors.add(error);
	}

	// TODO (Jun 15, 2009 10:00:10 PM): is this same as assertThat?
	public <T> void checkThat(T value, Matcher<T> matcher) {
		// TODO (Jun 15, 2009 10:01:28 PM): checkReturns
		try {
			assertThat(value, matcher);
		} catch (AssertionError e) {
			addError(e);
		}
	}

	public Object checkSucceeds(Callable<Object> callable) {
		try {
			return callable.call();
		} catch (Throwable e) {
			addError(e);
			return null;
		}		
	}
}