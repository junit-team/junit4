package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;


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
