/**
 * 
 */
package org.junit.experimental.theories.matchers.api;

public class StackTrace {
	public static StackTrace create() {
		return new StackTrace(new RuntimeException());
	}

	private final StackTraceElement[] stackTrace;

	private StackTrace(Throwable e) {
		this.stackTrace = e.getStackTrace();
	}

	public String factoryMethodName() {
		return nameOfMethodThatCalledMostRecentConstructor();
	}

	private String nameOfMethodThatCalledMostRecentConstructor() {
		boolean initSeen = false;
		for (StackTraceElement element : getElements()) {
			if (element.getMethodName().equals("<init>"))
				initSeen = true;
			else if (initSeen)
				return element.getMethodName();
		}

		return null;
	}

	public StackTraceElement[] getElements() {
		return stackTrace;
	}

}