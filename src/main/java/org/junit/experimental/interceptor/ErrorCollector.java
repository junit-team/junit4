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

	public <T> void checkThat(final T value, final Matcher<T> matcher) {
		checkSucceeds(new Callable<Object>() {
			public Object call() throws Exception {
				assertThat(value, matcher);
				return value;
			}
		});
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