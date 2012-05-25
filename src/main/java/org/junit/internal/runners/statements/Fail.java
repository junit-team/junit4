package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;

/**
 * @since 4.5
 */
public class Fail extends Statement {
	private final Throwable fError;

	public Fail(Throwable e) {
		fError= e;
	}

	@Override
	public void evaluate() throws Throwable {
		throw fError;
	}
}
