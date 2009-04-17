/**
 * 
 */
package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;

public class FailOnTimeout extends Statement {
	private Statement fNext;

	private final long fTimeout;

	private boolean fFinished= false;

	private Throwable fThrown= null;

	public FailOnTimeout(Statement next, long timeout) {
		fNext= next;
		fTimeout= timeout;
	}

	@Override
	public void evaluate() throws Throwable {
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