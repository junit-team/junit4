/**
 * 
 */
package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;

public class FailOnTimeout extends Statement {
	private final Statement fNext;

	private final long fTimeout;

	private boolean fFinished;

	private Throwable fThrown;

	public FailOnTimeout(Statement next, long timeout) {
		fNext= next;
		fTimeout= timeout;
	}

	@Override
	public void evaluate() throws Throwable {
		fFinished= false;
		fThrown= null;
		Thread thread= new Thread() {
			@Override
			public void run() {
				try {
					fNext.evaluate();
					fFinished= true;
				} catch (Throwable e) {
					fThrown= e;
				}
			}
		};
		thread.start();
		thread.join(fTimeout);
		if (fFinished)
			return;
		if (fThrown != null)
			throw fThrown;
		Exception exception= new Exception(String.format(
				"test timed out after %d milliseconds", fTimeout));
		exception.setStackTrace(thread.getStackTrace());
		throw exception;
	}
}
