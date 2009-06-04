/**
 * 
 */
package org.junit.experimental.interceptor;

import java.util.ArrayList;
import java.util.List;

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

}