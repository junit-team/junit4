package org.junit.experimental.interceptor;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class ExpectedException implements StatementInterceptor {
	private Class<? extends Throwable> fType;
	private String fMessage;

	public Statement intercept(Statement base, FrameworkMethod method) {
		return new ExpectedExceptionStatement(base);
	}

	public void expect(Class<? extends Throwable> type) {
		fType= type;
	}

	public void expectMessage(String message) {
		fMessage= message;
	}

	private boolean noExpectedException() {
		return fType == null && fMessage == null;
	}
	
	public class ExpectedExceptionStatement extends Statement {

		private final Statement fNext;

		public ExpectedExceptionStatement(Statement base) {
			fNext= base;
		}

		@Override
		public void evaluate() throws Throwable {
			boolean complete = false;
			try {
				fNext.evaluate();
				complete = true;
			} catch (Throwable e) {
				if (noExpectedException())
					throw e;
				// TODO (May 26, 2009 11:46:31 PM): isInstance?
				if (fType != null && !fType.isAssignableFrom(e.getClass())) {
					String message= "Unexpected exception, expected<"
								+ fType.getName() + "> but was<"
								+ e.getClass().getName() + ">";
					throw new Exception(message, e);
				}
				if (fMessage != null && !getMessage(e).contains(fMessage)) {
					String message= "Unexpected exception message, expected<"
								+ fMessage + "> but was<"
								+ getMessage(e) + ">";
					throw new Exception(message, e);
				}
			}
			// TODO (May 26, 2009 11:54:22 PM): do I need complete
			if (complete && !noExpectedException()) {
				if (fType != null)
					throw new AssertionError("Expected exception: "
							+ fType.getName());
				else if (fMessage != null)
					throw new AssertionError("Expected exception with message: "
							+ fMessage);
				else
					throw new RuntimeException("How'd we get here?");
			}
		}

		private String getMessage(Throwable e) {
			return e.getMessage() == null ? "" : e.getMessage();
		}
	}
}
