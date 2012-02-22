// Copyright 2010 Google Inc. All Rights Reserved.

package org.junit.runners.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects multiple {@code Throwable}s into one exception.
 */
public class MultipleFailureException extends Exception {
	private static final long serialVersionUID= 1L;
	
	private final List<Throwable> fErrors;

	public MultipleFailureException(List<Throwable> errors) {
		fErrors= new ArrayList<Throwable>(errors);
	}

	public List<Throwable> getFailures() {
		return Collections.unmodifiableList(fErrors);
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder(
				String.format("There were %d errors:", fErrors.size()));
		for (Throwable e : fErrors) {
			sb.append(String.format("\n  %s(%s)", e.getClass().getName(), e.getMessage()));
		}
		return sb.toString();
	}

	/**
	 * Asserts that a list of throwables is empty. If it isn't empty,
	 * will throw {@link MultipleFailureException} (if there are
	 * multiple throwables in the list) or the first element in the list
	 * (if there is only one element).
	 * 
	 * @param errors list to check
	 * @throws Throwable if the list is not empty
	 */
	@SuppressWarnings("deprecation")
	public static void assertEmpty(List<Throwable> errors) throws Throwable {
		if (errors.isEmpty())
			return;
		if (errors.size() == 1)
			throw errors.get(0);

		/*
		 * Many places in the code are documented to throw
		 * org.junit.internal.runners.model.MultipleFailureException.
		 * That class now extends this one, so we throw the internal
		 * exception in case developers have tests that catch
		 * MultipleFailureException.
		 */
		throw new org.junit.internal.runners.model.MultipleFailureException(errors);
	}
}
