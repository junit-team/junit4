package org.junit.tests.running.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Permission;

public class MainRunner {

	private static class ExitException extends SecurityException {
		private static final long serialVersionUID= -9104651568237766642L;

		private final int status;

		public ExitException(int status) {
			super("");
			this.status= status;
		}

		public int getStatus() {
			return status;
		}
	}

	private static class NoExitSecurityManager extends SecurityManager {
		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}

		@Override
		public void checkPermission(Permission perm, Object context) {
			// allow anything.
		}

		@Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new ExitException(status);
		}
	}

	/**
	 * Execute runnable.run(), preventing System.exit(). If System.exit() is called
	 * in runnable.run(), the value is returned. If System.exit()
	 * is not called, null is returned.
	 * 
	 * @param runnable
	 * @return null if System.exit() is not called, Integer.valueof(status) if not
	 */
	public Integer runWithCheckForSystemExit(Runnable runnable) {
		SecurityManager oldSecurityManager = System.getSecurityManager();
		System.setSecurityManager(new NoExitSecurityManager());
		PrintStream oldPrintStream = System.out;

		System.setOut(new PrintStream(new ByteArrayOutputStream()));
		try {
			runnable.run();
			System.out.println("System.exit() not called, return null");
			return null;
		} catch (ExitException e) {
			System.out.println("System.exit() called, value=" + e.getStatus());
			return e.getStatus();
		} finally {
			System.setSecurityManager(oldSecurityManager);
			System.setOut(oldPrintStream);
		}
	}

}
