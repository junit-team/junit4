/**
 * 
 */
package org.junit.internal.runners.statements;

import org.junit.runners.model.Statement;

public class FailOnTimeout extends Statement {
	private final Statement fOriginalStatement;

	private final long fTimeout;

	public FailOnTimeout(Statement originalStatement, long timeout) {
		fOriginalStatement= originalStatement;
		fTimeout= timeout;
	}

	@Override
	public void evaluate() throws Throwable {
		StatementThread thread= evaluateStatement();
		if (!thread.fFinished)
			throwExceptionForUnfinishedThread(thread);
	}

	private StatementThread evaluateStatement() throws InterruptedException {
		StatementThread thread= new StatementThread(fOriginalStatement);
		thread.start();
		thread.join(fTimeout);
		thread.interrupt();
		return thread;
	}

	private void throwExceptionForUnfinishedThread(StatementThread thread)
			throws Throwable {
		if (thread.fExceptionThrownByOriginalStatement != null)
			throw thread.fExceptionThrownByOriginalStatement;
		else
			throwTimeoutException(thread);
	}

	private void throwTimeoutException(StatementThread thread) throws Exception {
		Exception exception= new Exception(String.format(
				"test timed out after %d milliseconds", fTimeout));
		exception.setStackTrace(thread.getStackTrace());
		throw exception;
	}

	private static class StatementThread extends Thread {
		private final Statement fStatement;

		private boolean fFinished= false;

		private Throwable fExceptionThrownByOriginalStatement= null;

		public StatementThread(Statement statement) {
			fStatement= statement;
		}

		@Override
		public void run() {
			try {
				fStatement.evaluate();
				fFinished= true;
			} catch (InterruptedException e) {
				//don't log the InterruptedException
			} catch (Throwable e) {
				fExceptionThrownByOriginalStatement= e;
			}
		}
	}
}